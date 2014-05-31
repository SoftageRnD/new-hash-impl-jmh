package memory;

import ru.softage.collection.mutable.ImmutableTrieBucketHashSet;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Utility class to inspect ImmutableTrieBucketHashSet table structure
 *
 * @author Misha Sokolov
 */
public class BucketsInspector {
    public static BucketsInfo inspectBuckets(ImmutableTrieBucketHashSet set, Class<?> elementClass)
            throws NoSuchFieldException, IllegalAccessException {
        BucketsInfo bucketsInfo = new BucketsInfo();

        bucketsInfo.setSize = set.size();

        Object[] table = (Object[]) getFieldValue(set,
                                                  "ru$softage$collection$mutable$ImmutableTrieBucketHashSet$$table");

        bucketsInfo.tableSize = table.length;

        bucketsInfo.nonEmptyBuckets = 0;
        for (Object obj : table) {
            if (obj != null) {
                bucketsInfo.nonEmptyBuckets++;
                if (elementClass.isAssignableFrom(obj.getClass())) {
                    incrementOnBucketSize(bucketsInfo.bucketSizeToCount, 1);
                } else {
                    incrementOnBucketSize(bucketsInfo.bucketSizeToCount, getBucketSize(obj));
                }
            }
        }

        bucketsInfo.averageBucketSizePerElement = 0;
        for (Integer size : bucketsInfo.bucketSizeToCount.keySet()) {
            int buckets = bucketsInfo.bucketSizeToCount.get(size);
            bucketsInfo.averageBucketSizePerElement += buckets * size * size;
        }
        bucketsInfo.averageBucketSizePerElement /= set.size();

        return bucketsInfo;
    }

    private static int getBucketSize(Object bucket) throws NoSuchFieldException, IllegalAccessException {
        scala.collection.Set bucketSet = (scala.collection.Set) getFieldValue(bucket, "set");
        return bucketSet.size();
    }

    private static Object getFieldValue(Object obj, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field tableField = obj.getClass().getDeclaredField(fieldName);
        tableField.setAccessible(true);
        return tableField.get(obj);
    }

    private static void incrementOnBucketSize(Map<Integer, Integer> map, int size) {
        if (!map.containsKey(size)) {
            map.put(size, 1);
        } else {
            map.put(size, map.get(size) + 1);
        }
    }

    public static class BucketsInfo {
        int setSize;
        int tableSize;
        int nonEmptyBuckets = 0;
        final SortedMap<Integer, Integer> bucketSizeToCount = new TreeMap<>();
        double averageBucketSizePerElement = 0;
    }
}
