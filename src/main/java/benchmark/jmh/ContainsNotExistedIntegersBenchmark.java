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
public class ContainsNotExistedIntegersBenchmark {
    @GenerateMicroBenchmark
    public void scalaSet(BlackHole bl, IntegersState state) {
        for (Integer key : state.notExistedKeys) {
            bl.consume(state.scalaSet.contains(key));
        }
    }

    @GenerateMicroBenchmark
    public void immutableTrieBucketSet(BlackHole bl, IntegersState state) {
        for (Integer key : state.notExistedKeys) {
            bl.consume(state.immutableTrieBucketSet.contains(key));
        }
    }

    @GenerateMicroBenchmark
    public void listBucketSet(BlackHole bl, IntegersState state) {
        for (Integer key : state.notExistedKeys) {
            bl.consume(state.listBucketSet.contains(key));
        }
    }
}
