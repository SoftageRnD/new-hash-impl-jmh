package benchmark.jmh;

import scala.collection.mutable.Set;

import java.util.Arrays;
import java.util.HashSet;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Misha Sokolov
 */
public class IntegersStateTest {
    private static final int SIZE = 500000;

    private IntegersState state;

    @Before
    public void setUp() {
        state = new IntegersState();
        state.size = SIZE;
    }

    @Test
    public void generateData() {
        state.generateData();

        assertKeysGenerated(state.keys);
        assertKeysGenerated(state.notExistedKeys);
        assertSetGenerated(state.scalaSet);
        assertSetGenerated(state.immutableTrieBucketSet);
        assertSetGenerated(state.listBucketSet);
    }

    private void assertKeysGenerated(Integer[] keys) {
        assertNotNull(keys);
        assertEquals(IntegersState.KEYS_SIZE, keys.length);
        for (Integer key : keys) {
            assertNotNull(key);
        }
        java.util.HashSet<Integer> uniqueKeys = new HashSet<Integer>(Arrays.asList(keys));
        assertEquals(keys.length, uniqueKeys.size());
    }

    private void assertSetGenerated(Set<Integer> set) {
        assertNotNull(set);
        assertEquals(SIZE, set.size());
        assertAllExists(state.keys, set);
        assertAllNotExists(state.notExistedKeys, set);
    }

    private void assertAllExists(Integer[] keys, Set<Integer> set) {
        for (Integer key : keys) {
            assertTrue(set.contains(key));
        }
    }

    private void assertAllNotExists(Integer[] keys, Set<Integer> set) {
        for (Integer key : keys) {
            assertFalse(set.contains(key));
        }
    }
}
