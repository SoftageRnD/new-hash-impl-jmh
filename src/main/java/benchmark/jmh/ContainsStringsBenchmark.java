package benchmark.jmh;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.logic.BlackHole;

/**
 * @author Misha Sokolov
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ContainsStringsBenchmark {
    @GenerateMicroBenchmark
    public void scalaSet(BlackHole bl, StringsState state) {
        for (String key : state.keys) {
            bl.consume(state.scalaSet.contains(key));
        }
    }

    @GenerateMicroBenchmark
    public void immutableTrieBucketSet(BlackHole bl, StringsState state) {
        for (String key : state.keys) {
            bl.consume(state.immutableTrieBucketSet.contains(key));
        }
    }

    @GenerateMicroBenchmark
    public void listBucketSet(BlackHole bl, StringsState state) {
        for (String key : state.keys) {
            bl.consume(state.listBucketSet.contains(key));
        }
    }
}
