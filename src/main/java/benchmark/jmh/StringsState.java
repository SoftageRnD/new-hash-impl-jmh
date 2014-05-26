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
    @Param({"1000", "3947", "6895", "9842", "12789", "15737", "18684", "21632", "24579", "27526", "30474", "33421", "36368", "39316", "42263", "45211", "48158", "51105", "54053", "57000"})
    public int size;

    private Random random = new Random();
    private ArrayList<String> existingKeys;
    private ArrayList<String> notExistingKeys = new ArrayList<>(KEYS_SIZE);
    private int existingKeyIndex;
    private int notExistingKeyIndex;

    public scala.collection.mutable.HashSet<String> scalaSet;
    public ImmutableTrieBucketHashSet<String> immutableTrieBucketSet;
    public ListBucketHashSet<String> listBucketSet;

    public String existingKey;
    public String notExistingKey;

    @Setup(Level.Trial)
    public void generateData() {
        random.setSeed(42L);
        scalaSet = new HashSet<>();
        immutableTrieBucketSet = new ImmutableTrieBucketHashSet<>();
        listBucketSet = new ListBucketHashSet<>();
        existingKeys = new ArrayList<>(size);
        notExistingKeys.clear();
        existingKeyIndex = 0;
        notExistingKeyIndex = 0;

        for (int i = 0; i < size; ++i) {
            String key = IDENTIFIERS.get(i);
            existingKeys.add(key);
            scalaSet.add(key);
            immutableTrieBucketSet.add(key);
            listBucketSet.add(key);
        }
        for (int i = 0; i < KEYS_SIZE; ++i) {
            notExistingKeys.add(IDENTIFIERS.get(size + i));
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
}
