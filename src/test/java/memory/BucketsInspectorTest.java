package memory;

import ru.softage.collection.mutable.ImmutableTrieBucketHashSet;

import org.junit.Test;

import static memory.BucketsInspector.BucketsInfo;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class BucketsInspectorTest {
    @Test
    public void inspectBuckets() throws NoSuchFieldException, IllegalAccessException {
        ImmutableTrieBucketHashSet<Integer> set = new ImmutableTrieBucketHashSet<>();
        for (int i = 0; i < 10; ++i) {
            set.add(i);
        }

        BucketsInfo bucketsInfo = BucketsInspector.inspectBuckets(set, Integer.class);

        assertThat(bucketsInfo.setSize, is(set.size()));
        assertThat(bucketsInfo.tableSize, greaterThan(0));
        assertThat(bucketsInfo.nonEmptyBuckets, greaterThan(0));
        assertThat(bucketsInfo.nonEmptyBuckets, lessThanOrEqualTo(bucketsInfo.tableSize));
        assertThat(bucketsInfo.averageBucketSizePerElement, greaterThanOrEqualTo(1.));

        assertThat(bucketsInfo.bucketSizeToCount, is(notNullValue()));
        assertThat(bucketsInfo.bucketSizeToCount.size(), greaterThan(0));
        int nonEmptyBucketsCountedFromMap = 0;
        int elementsCountedFromMap = 0;
        for (Integer bucketSize : bucketsInfo.bucketSizeToCount.keySet()) {
            Integer count = bucketsInfo.bucketSizeToCount.get(bucketSize);
            nonEmptyBucketsCountedFromMap += count;
            elementsCountedFromMap += bucketSize * count;
        }
        assertThat(nonEmptyBucketsCountedFromMap, is(bucketsInfo.nonEmptyBuckets));
        assertThat(elementsCountedFromMap, is(bucketsInfo.setSize));
    }
}
