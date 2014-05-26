package benchmark.jmh;

import ru.softage.collection.mutable.ImmutableTrieBucketHashSet;
import ru.softage.collection.mutable.ListBucketHashSet;
import scala.collection.mutable.Set;

import java.util.ArrayList;
import java.util.HashSet;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static benchmark.jmh.BenchmarkTestUtils.getStateParamValues;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

/**
 * @author Misha Sokolov
 */
@RunWith(Parameterized.class)
public class IntegersStateTest {
    @Parameterized.Parameters(name = "{0}")
    public static Iterable<Object[]> extractStateParameters() throws NoSuchFieldException {
        ArrayList<Object[]> params = new ArrayList<>();
        for (String stateSizeString : getStateParamValues(IntegersState.class, "size")) {
            params.add(new Object[]{Integer.valueOf(stateSizeString)});
        }
        return params;
    }

    @Parameterized.Parameter
    public Integer size;

    private IntegersState state;

    @Before
    public void setUp() {
        state = new IntegersState();
        state.size = size;
    }

    @Test
    public void runBenchmarkOnce() {
        state.generateData();
        assertAllSetsGenerated();
        iterateBenchmark10000Times();
    }

    @Test
    public void runBenchmarkTwice() {
        state.generateData();
        assertAllSetsGenerated();
        iterateBenchmark10000Times();

        state.generateData();
        assertAllSetsGenerated();
        iterateBenchmark10000Times();
    }

    @Test
    public void setsShouldBeReinstantiatedWhenDataGenerated() {
        state.generateData();

        scala.collection.mutable.HashSet<Integer> scalaSet = state.scalaSet;
        ImmutableTrieBucketHashSet<Integer> immutableTrieBucketSet = state.immutableTrieBucketSet;
        ListBucketHashSet<Integer> listBucketSet = state.listBucketSet;

        state.generateData();

        assertTrue(scalaSet != state.scalaSet);
        assertTrue(immutableTrieBucketSet != state.immutableTrieBucketSet);
        assertTrue(listBucketSet != state.listBucketSet);
    }

    private void iterateBenchmark10000Times() {
        HashSet<Integer> existingKeys = new HashSet<>();
        HashSet<Integer> notExistingKeys = new HashSet<>();
        for (int i = 0; i < 10000; ++i) {
            state.pickKeys();

            assertThat(state.existingKey, is(notNullValue()));
            assertExistsInAllSets(state.existingKey);
            existingKeys.add(state.existingKey);

            assertThat(state.notExistingKey, is(notNullValue()));
            assertNotExistsInAllSets(state.notExistingKey);
            notExistingKeys.add(state.notExistingKey);
        }

        assertThat(existingKeys, hasSize(greaterThanOrEqualTo(1000)));
        assertThat(notExistingKeys, hasSize(greaterThanOrEqualTo(1000)));
    }

    private void assertAllSetsGenerated() {
        assertSetGenerated(state.scalaSet);
        assertSetGenerated(state.immutableTrieBucketSet);
        assertSetGenerated(state.listBucketSet);
    }

    private void assertExistsInAllSets(Integer key) {
        assertTrue(state.immutableTrieBucketSet.contains(key));
        assertTrue(state.listBucketSet.contains(key));
        assertTrue(state.scalaSet.contains(key));
    }

    private void assertNotExistsInAllSets(Integer key) {
        assertFalse(state.immutableTrieBucketSet.contains(key));
        assertFalse(state.listBucketSet.contains(key));
        assertFalse(state.scalaSet.contains(key));
    }

    private void assertSetGenerated(Set<Integer> set) {
        assertThat(set, is(Matchers.notNullValue()));
        assertEquals((int) size, set.size());
    }
}
