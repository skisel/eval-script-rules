package com.skisel.benchmarks;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * User: sergeykisel
 * Date: 24.03.14
 * Time: 19:39
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class RhinoBenchmark {

    private com.skisel.Context context;

    @Param({"-1", "0", "5", "9"})
    public String level;
    public Integer optLevel;
    private Context cx;
    private Scriptable scope;
    private Script script;

    @Setup
    public void setUp() {
        optLevel = Integer.valueOf(level);
        context = new com.skisel.Context();
        double a = 1.0;
        double b = 2.0;
        context.write("a", a);
        context.write("b", b);


        cx = Context.enter();
        cx.setOptimizationLevel(optLevel);
        cx.setLanguageVersion(0);
        cx.getWrapFactory().setJavaPrimitiveWrap(false);
        scope = cx.initStandardObjects(null, true);
        scope.put("ctx", scope, context);
        script = cx.compileString("ctx.read('a') + ctx.read('b');", "<cmd>", 1, null);

    }

    @TearDown
    public void tearDown() {
        Context.exit();
    }

    public Object eval(com.skisel.Context ctx) {
        Context cx = Context.enter();
        cx.setOptimizationLevel(optLevel);
        cx.setLanguageVersion(0);
        cx.getWrapFactory().setJavaPrimitiveWrap(false);
        try {
            Scriptable scope = cx.initStandardObjects(null, true);
            scope.put("ctx", scope, ctx);
            return cx.evaluateString(scope, "ctx.read('a') + ctx.read('b');", "<cmd>", 1, null);
        } finally {
            Context.exit();
        }
    }

    @GenerateMicroBenchmark
    public void evalRhino() {
        eval(context);
    }

    @GenerateMicroBenchmark
    public void eval() {
        cx.evaluateString(scope, "ctx.read('a') + ctx.read('b');", "<cmd>", 1, null);
    }

    @GenerateMicroBenchmark
    public void compile() {
        cx.compileString("ctx.read('a') + ctx.read('b');", "<cmd>", 1, null);
    }

    @GenerateMicroBenchmark
    public void evalCompiled() {
        script.exec(cx, scope);
    }


}
