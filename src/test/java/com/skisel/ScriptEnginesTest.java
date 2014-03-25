package com.skisel;

import com.skisel.benchmarks.RhinoBenchmark;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.script.*;
import java.util.Set;
import java.util.concurrent.*;

/**
 * User: sergeykisel
 * Date: 17.03.14
 * Time: 12:47
 */
public class ScriptEnginesTest {

    private static ScriptEngineManager manager = new ScriptEngineManager();

    @Before
    public void before() {
        System.setProperty("scala.usejavacp", "true");
    }

    @Test
    public void testRhino() {
        Context context = new Context();
        context.write("a", 1.0);
        context.write("b", 2.0);
        RhinoBenchmark rhinoBenchmark = new RhinoBenchmark();
        rhinoBenchmark.level ="-1";
        rhinoBenchmark.setUp();
        Assert.assertEquals(3.0, rhinoBenchmark.eval(context));
    }

    @Test
    public void testJs() throws ScriptException {
        ScriptEngine engine = manager.getEngineByName("javascript");
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        Context context = new Context();
        context.write("a", 1.0);
        context.write("b", 2.0);
        bindings.put("ctx", context);
        Assert.assertEquals(3.0, engine.eval("ctx.read('a') + ctx.read('b');"));
    }

    @Test
    public void testScala() throws ScriptException {
        ScriptEngine engine = manager.getEngineByName("scala");
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        Context context = new Context();
        context.write("a", 1.0);
        context.write("b", 2.0);
        bindings.put("ctx", context);
        Assert.assertEquals(3.0, engine.eval("import com.skisel.Context; ctx.asInstanceOf[Context].read(\"a\").asInstanceOf[java.lang.Double] + ctx.asInstanceOf[Context].read(\"b\").asInstanceOf[java.lang.Double];"));
    }

    @Test
    public void testScalaReadDouble() throws ScriptException {
        ScriptEngine engine = manager.getEngineByName("scala");
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        Context context = new Context();
        context.write("a", 1.0);
        context.write("b", 2.0);
        bindings.put("ctx", context);
        Assert.assertEquals(3.0, engine.eval("import com.skisel.Context; ctx.asInstanceOf[Context].readDouble(\"a\")+ ctx.asInstanceOf[Context].readDouble(\"b\")"));
    }

    @Test
    public void testGroovy() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("groovy");
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        Context context = new Context();
        context.write("a", 1.0);
        context.write("b", 2.0);
        bindings.put("ctx", context);
        Assert.assertEquals(3.0, engine.eval("ctx.read('a') + ctx.read('b');"));
    }

    @Test
    public void testEngineEquality() {
        assertEnginesAreEqual("scala");
        assertEnginesAreEqual("JavaScript");
        assertEnginesAreEqual("groovy");

    }

    @Test
    public void testSameEngineSharesNamespaceAndVars() throws ScriptException {
        ScriptEngine engine = manager.getEngineByName("scala");
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        Context context = new Context();
        context.write("a", 1.0);
        context.write("b", 2.0);
        bindings.put("ctx", context);
        engine.eval("import com.skisel.Context; ");
        engine.eval("val c = ctx.asInstanceOf[Context];");
        Assert.assertEquals(3.0, engine.eval("c.readDouble(\"a\")+c.readDouble(\"b\")"));
    }


    private void assertEnginesAreEqual(String scala) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName(scala);
        ScriptEngine engine2 = manager.getEngineByName(scala);
        Assert.assertFalse(engine == engine2);
        Assert.assertFalse(engine.getBindings(ScriptContext.ENGINE_SCOPE) == engine2.getBindings(ScriptContext.ENGINE_SCOPE));
    }

    @Test
    public void testDifferentThreadCompiledScript() throws InterruptedException, ScriptException, TimeoutException, ExecutionException {
        Future<CompiledScript> scriptFuture = Executors.newSingleThreadExecutor().submit(getCompiledScriptRunnable(1.0, 2.0));
        CompiledScript script = scriptFuture.get(10, TimeUnit.SECONDS);
        Assert.assertNotNull("Script is not yet compiled. Most probably not yet ready", script);
        Assert.assertEquals(3.0, script.eval());
    }

    @Test
    public void testTwoThreadsCompilation() throws Exception {
        Future<CompiledScript> s1 = Executors.newSingleThreadExecutor().submit(getCompiledScriptRunnable(1.0, 2.0));
        Future<CompiledScript> s2 = Executors.newSingleThreadExecutor().submit(getCompiledScriptRunnable(15.5, 16.5));
        CompiledScript script1 = s1.get(10, TimeUnit.SECONDS);
        CompiledScript script2 = s2.get(10, TimeUnit.SECONDS);
        Assert.assertNotNull("Script is not yet compiled. Most probably not yet ready", script1);
        Assert.assertNotNull("Script is not yet compiled. Most probably not yet ready", script2);
        Assert.assertEquals(3.0, script1.eval());
        Assert.assertEquals(32.0, script2.eval());
    }

    private Callable<CompiledScript> getCompiledScriptRunnable(final double a, final double b) {
        return new Callable<CompiledScript>() {
            @Override
            public CompiledScript call() throws Exception {
                ScriptEngine engine = manager.getEngineByName("scala");
                Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
                Context context = new Context();
                context.write("a", a);
                context.write("b", b);
                bindings.put("ctx", context);
                return ((Compilable) engine).compile("import com.skisel.Context; ctx.asInstanceOf[Context].readDouble(\"a\")+ ctx.asInstanceOf[Context].readDouble(\"b\")");
            }
        };
    }

    @Test
    //todo why it fails ?
    public void testClassLoadersIssue() throws ScriptException {
        assertRuleIsCorrect();
        assertRuleIsCorrect();
    }

    private void assertRuleIsCorrect() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine scalaEngine = manager.getEngineByName("scala");
        Bindings bindings = scalaEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        Context context = new Context();
        context.write("a", 1.0);
        context.write("b", 2.0);
        bindings.put("ctx", context);
        Assert.assertEquals(3.0, scalaEngine.eval("import com.skisel.Context; ctx.asInstanceOf[Context].readDouble(\"a\")+ ctx.asInstanceOf[Context].readDouble(\"b\")"));
    }

}
