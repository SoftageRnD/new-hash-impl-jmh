package memory;

import benchmark.jmh.IntegersState;
import benchmark.jmh.StringsState;

import static java.lang.String.format;
import static memory.BucketsInspector.BucketsInfo;
import static memory.BucketsInspector.inspectBuckets;

/**
 * Sample app which prints ImmutableTrieBucketHashSet structure with different data sets
 */
public class InspectBuckets {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        System.out.println("----Integers----");
        IntegersState integersState = new IntegersState();
        for (int size : new int[]{15737, 24579, 30474, 45211}) {
            integersState.size = size;
            integersState.generateData();
            printBucketsInfo(inspectBuckets(integersState.immutableTrieBucketSet, Integer.class));
            System.out.println("----------");
        }

        System.out.println("----Strings----");
        StringsState stringsState = new StringsState();
        for (int size : new int[]{9842, 21632, 33421, 57000}) {
            stringsState.size = size;
            stringsState.generateData();
            printBucketsInfo(inspectBuckets(stringsState.immutableTrieBucketSet, String.class));
            System.out.println("----------");
        }
    }

    private static void printBucketsInfo(BucketsInfo bucketsInfo) {
        System.out.println("Set size: " + bucketsInfo.setSize);
        System.out.println("Table size: " + bucketsInfo.tableSize);
        System.out.println(format("Non-empty buckets: %d (%.1f%%)",
                                  bucketsInfo.nonEmptyBuckets,
                                  getPercentage(bucketsInfo.nonEmptyBuckets,
                                                bucketsInfo.tableSize)));
        for (Integer size : bucketsInfo.bucketSizeToCount.keySet()) {
            int buckets = bucketsInfo.bucketSizeToCount.get(size);
            System.out.println(format("Bucket %d: %d (%.1f%% of non-empty buckets, %.1f%% of elements)",
                                      size,
                                      buckets,
                                      getPercentage(buckets, bucketsInfo.nonEmptyBuckets),
                                      getPercentage(buckets * size, bucketsInfo.setSize)));
        }
        System.out.println(format("Average bucket size per element: %.2f", bucketsInfo.averageBucketSizePerElement));
    }

    private static float getPercentage(int a, int b) {
        return ((float) a) / b * 100;
    }
}
