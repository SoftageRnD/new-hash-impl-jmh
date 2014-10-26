package memory;

import benchmark.jmh.IntegersState;
import benchmark.jmh.StringsState;
import org.openjdk.jmh.annotations.Param;
import ru.softage.collection.mutable.ImmutableTrieBucketHashSet;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static memory.BucketsInspector.BucketsInfo;
import static memory.BucketsInspector.inspectBuckets;

/**
 * App which inspects ImmutableTrieBucketHashSet's internals in StringsState and IntegerState, calculates
 * average bucket size per element and prints it out to CSV files.
 * <p/>
 * One argument: directory to write result csv files in. If not provided the current working directory is used.
 * <p/>
 * Output files:
 * <li>for StringsState: hash-set-with-strings-buckets-size.csv</li>
 * <li>for IntegersState: hash-set-with-integers-buckets-size.csv</li>
 *
 * @author Misha Sokolov
 */
public class InspectAverageBucketSizePerElement {
    public static void main(String[] args) throws IOException, ReflectiveOperationException {
        File resultDir = getResultDirectoryArg(args);
        System.out.println("Inspecting buckets for hash set in");
        System.out.print("\t- StringsState... ");
        inspectState(StringsState.class, String.class, new File(resultDir, "hash-set-with-strings-buckets-size.csv"));
        System.out.println("done.");
        System.out.print("\t- IntegersState... ");
        inspectState(IntegersState.class, Integer.class, new File(resultDir, "hash-set-with-integers-buckets-size.csv"));
        System.out.println("done.");
    }

    private static File getResultDirectoryArg(String[] args) {
        if (args.length < 1) {
            return new File(".");
        }
        return new File(args[0]);
    }

    private static void inspectState(Class<?> stateClass, Class<?> setElementClass, File resultFile)
            throws ReflectiveOperationException, IOException {

        Field sizeField = stateClass.getDeclaredField("size");
        Field hashSetToInspectField = stateClass.getDeclaredField("immutableTrieBucketSet");
        Method generateDataMethod = stateClass.getDeclaredMethod("generateData");
        String[] sizesStrings = sizeField.getAnnotation(Param.class).value();
        try (PrintWriter fileWriter = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(resultFile), StandardCharsets.UTF_8))) {

            fileWriter.println("size,averageBucketSizePerElement");
            for (String sizeString : sizesStrings) {
                Object stateInstance = stateClass.newInstance();
                int size = Integer.parseInt(sizeString);
                sizeField.setInt(stateInstance, size);
                generateDataMethod.invoke(stateInstance);
                ImmutableTrieBucketHashSet hashSet = (ImmutableTrieBucketHashSet) hashSetToInspectField.get(stateInstance);
                BucketsInfo bucketsInfo = inspectBuckets(hashSet, setElementClass);
                fileWriter.println(size + "," + bucketsInfo.averageBucketSizePerElement);
            }
        }
    }
}
