package benchmark.jmh;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Misha Sokolov
 */
public class BenchmarkTestUtils {
    private BenchmarkTestUtils() {
        //not needed
    }

    public static List<String> getStateParamValues(Class<?> stateClass,
                                                   String paramFieldName) throws NoSuchFieldException {
        ArrayList<String> values = new ArrayList<>();
        Field stateSizeField = stateClass.getDeclaredField(paramFieldName);
        for (Annotation annotation : stateSizeField.getDeclaredAnnotations()) {
            if (annotation instanceof org.openjdk.jmh.annotations.Param) {
                org.openjdk.jmh.annotations.Param jmhParam = (org.openjdk.jmh.annotations.Param) annotation;
                Collections.addAll(values, jmhParam.value());
                break;
            }
        }
        return values;
    }
}
