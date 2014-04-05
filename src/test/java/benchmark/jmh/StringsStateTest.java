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
public class StringsStateTest {
    private static final int SIZE = 55000;

    private StringsState state;

    @Before
    public void setUp() {
        state = new StringsState();
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

    private void assertKeysGenerated(String[] keys) {
        assertNotNull(keys);
        assertEquals(StringsState.KEYS_SIZE, keys.length);
        for (String key : keys) {
            assertNotNull(key);
        }
        java.util.HashSet<String> uniqueKeys = new HashSet<>(Arrays.asList(keys));
        assertEquals(keys.length, uniqueKeys.size());
    }

    private void assertSetGenerated(Set<String> set) {
        assertNotNull(set);
        assertEquals(SIZE, set.size());
        assertAllExists(state.keys, set);
        assertAllNotExists(state.notExistedKeys, set);
    }

    private void assertAllExists(String[] keys, Set<String> set) {
        for (String key : keys) {
            assertTrue(set.contains(key));
        }
    }

    private void assertAllNotExists(String[] keys, Set<String> set) {
        for (String key : keys) {
            assertFalse(set.contains(key));
        }
    }
}
