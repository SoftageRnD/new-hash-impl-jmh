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
    private static final int KEYS_SIZE = 1000;

    @Param({"1000", "3947", "6895", "9842", "12789", "15737", "18684", "21632", "24579", "27526", "30474", "33421", "36368", "39316", "42263", "45211", "48158", "51105", "54053", "57000"})
    public int size;

    private Random random = new Random();
    private ArrayList<Integer> existingKeys;
    private ArrayList<Integer> notExistingKeys = new ArrayList<>(KEYS_SIZE);
    private int existingKeyIndex;
    private int notExistingKeyIndex;

    public scala.collection.mutable.HashSet<Integer> scalaSet;
    public ImmutableTrieBucketHashSet<Integer> immutableTrieBucketSet;
    public ListBucketHashSet<Integer> listBucketSet;

    public Integer existingKey;
    public Integer notExistingKey;

    @Setup(Level.Trial)
    public void generateData() {
        random.setSeed(-23252250L);
        scalaSet = new HashSet<>();
        immutableTrieBucketSet = new ImmutableTrieBucketHashSet<>();
        listBucketSet = new ListBucketHashSet<>();
        existingKeys = new ArrayList<>(size);
        notExistingKeys.clear();
        existingKeyIndex = 0;
        notExistingKeyIndex = 0;

        ArrayList<Integer> data = generateDistinctRandomNums(random, size + KEYS_SIZE);
        for (int i = 0; i < data.size(); ++i) {
            Integer key = data.get(i);
            if (i < size) {
                existingKeys.add(key);
                scalaSet.add(key);
                immutableTrieBucketSet.add(key);
                listBucketSet.add(key);
            } else {
                notExistingKeys.add(key);
            }
        }

        Collections.shuffle(existingKeys, random);
        Collections.shuffle(notExistingKeys, random);
    }

    @Setup(Level.Iteration)
    public void pickKeys() {
        existingKey = existingKeys.get(existingKeyIndex);
        notExistingKey = notExistingKeys.get(notExistingKeyIndex);

        existingKeyIndex = (existingKeyIndex + 1) % existingKeys.size();
        notExistingKeyIndex = (notExistingKeyIndex + 1) % notExistingKeys.size();
    }

    private ArrayList<Integer> generateDistinctRandomNums(Random random, int size) {
        java.util.HashSet<Integer> set = new java.util.HashSet<>(size);
        while (set.size() != size) {
            set.add(random.nextInt());
        }
        ArrayList<Integer> result = new ArrayList<>(set);
        Collections.shuffle(result, random);
        return result;
    }
}
