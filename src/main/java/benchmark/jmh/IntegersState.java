package benchmark.jmh;

import ru.softage.collection.mutable.ImmutableTrieBucketHashSet;
import ru.softage.collection.mutable.ListBucketHashSet;
import scala.collection.mutable.HashSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * @author Misha Sokolov
 */
@State(Scope.Thread)
public class IntegersState {
    public static final int KEYS_SIZE = 1000;

    @Param({"100000", "200000", "300000", "400000", "500000", "600000", "700000", "800000", "900000", "1000000"})
    public int size;

    public scala.collection.mutable.HashSet<Integer> scalaSet = new HashSet<Integer>();
    public ImmutableTrieBucketHashSet<Integer> immutableTrieBucketSet = new ImmutableTrieBucketHashSet<Integer>();
    public ListBucketHashSet<Integer> listBucketSet = new ListBucketHashSet<Integer>();

    public Integer[] keys = new Integer[KEYS_SIZE];
    public Integer[] notExistedKeys = new Integer[KEYS_SIZE];

    @Setup(Level.Trial)
    public void generateData() {
        Random random = new Random(42L);

        ArrayList<Integer> data = generateDistinctRandomNums(random, size + KEYS_SIZE);
        ArrayList<Integer> existedData = new ArrayList<Integer>(size);
        ArrayList<Integer> notExistedData = new ArrayList<Integer>(KEYS_SIZE);
        for (int i = 0; i < data.size(); ++i) {
            Integer key = data.get(i);
            if (i < size) {
                existedData.add(key);
                scalaSet.add(key);
                immutableTrieBucketSet.add(key);
                listBucketSet.add(key);
            } else {
                notExistedData.add(key);
            }
        }

        Collections.shuffle(existedData, random);
        for (int i = 0; i < KEYS_SIZE; ++i) {
            keys[i] = existedData.get(i);
        }

        for (int i = 0; i < KEYS_SIZE; ++i) {
            notExistedKeys[i] = notExistedData.get(i);
        }
    }

    private ArrayList<Integer> generateDistinctRandomNums(Random random, int size) {
        java.util.HashSet<Integer> set = new java.util.HashSet<Integer>(size);
        while (set.size() != size) {
            set.add(random.nextInt());
        }
        ArrayList<Integer> result = new ArrayList<Integer>(set);
        Collections.shuffle(result, random);
        return result;
    }
}
