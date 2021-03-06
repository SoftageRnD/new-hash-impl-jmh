package benchmark.jmh;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

/**
 * @author Misha Sokolov
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ContainsNotExistedIntegersBenchmark {
    @GenerateMicroBenchmark
    public boolean scalaSet(IntegersState state) {
        return state.scalaSet.contains(state.notExistingKey);
    }

    @GenerateMicroBenchmark
    public boolean immutableTrieBucketSet(IntegersState state) {
        return state.immutableTrieBucketSet.contains(state.notExistingKey);
    }

    @GenerateMicroBenchmark
    public boolean listBucketSet(IntegersState state) {
        return state.listBucketSet.contains(state.notExistingKey);
    }
}
