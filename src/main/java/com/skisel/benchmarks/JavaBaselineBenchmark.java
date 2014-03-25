package com.skisel.benchmarks;

import com.skisel.Context;
import org.openjdk.jmh.annotations.*;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.concurrent.TimeUnit;

/**
 * User: sergeykisel
 * Date: 18.03.14
 * Time: 9:41
 */

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class JavaBaselineBenchmark {

    private Context context;

    @Setup
    public void setup() throws ScriptException {
        context = new Context();
        context.write("a", 1.0);
        context.write("b", 2.0);
    }

    @GenerateMicroBenchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void javaBaseline() {
        double v = context.readDouble("a") + context.readDouble("b");
        assert new Double(3.0d).equals(v);
    }


    @GenerateMicroBenchmark
    public void donothingBaseline() {
        // do nothing, this is a baseline
    }
}
