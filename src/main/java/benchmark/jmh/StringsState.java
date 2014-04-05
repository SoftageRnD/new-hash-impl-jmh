package benchmark.jmh;

import ru.softage.collection.mutable.ImmutableTrieBucketHashSet;
import ru.softage.collection.mutable.ListBucketHashSet;
import scala.collection.mutable.HashSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * @author Misha Sokolov
 */
@State(Scope.Thread)
public class StringsState {
    public static final int KEYS_SIZE = 1000;

    private static final ArrayList<String> IDENTIFIERS = readIdentifiers();

    private static ArrayList<String> readIdentifiers() {
        try (Scanner scanner = new Scanner(StringsState.class.getResourceAsStream("/identifiers.txt"))) {
            ArrayList<String> result = new ArrayList<>();
            while (scanner.hasNextLine()) {
                result.add(scanner.nextLine());
            }
            return result;
        }
    }

    // total size of available identifiers set = 58422 (/identifiers.txt)
    @Param({"10000", "15000", "20000", "25000", "30000", "35000", "40000", "45000", "50000", "55000"})
    public int size;

    public scala.collection.mutable.HashSet<String> scalaSet = new HashSet<>();
    public ImmutableTrieBucketHashSet<String> immutableTrieBucketSet = new ImmutableTrieBucketHashSet<>();
    public ListBucketHashSet<String> listBucketSet = new ListBucketHashSet<>();

    public String[] keys = new String[KEYS_SIZE];
    public String[] notExistedKeys = new String[KEYS_SIZE];

    @Setup(Level.Trial)
    public void generateData() {
        Random random = new Random(42L);

        ArrayList<String> existedData = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            String key = IDENTIFIERS.get(i);
            existedData.add(key);
            scalaSet.add(key);
            immutableTrieBucketSet.add(key);
            listBucketSet.add(key);
        }
        Collections.shuffle(existedData, random);
        for (int i = 0; i < KEYS_SIZE; ++i) {
            keys[i] = existedData.get(i);
        }

        for (int i = 0; i < KEYS_SIZE; ++i) {
            notExistedKeys[i] = IDENTIFIERS.get(size + i);
        }
    }
}
