package com.skisel.benchmarks;

import com.skisel.Context;

import javax.script.*;

/**
 * User: sergeykisel
 * Date: 17.03.14
 * Time: 12:46
 */
class RuleEvaluator {
    private ScriptEngine engine;
    private String script;
    private CompiledScript compiledScript;

    protected RuleEvaluator(ScriptEngine engine, String script, Context context) throws ScriptException {
        if (engine == null) throw new IllegalArgumentException("no engine defined");
        this.engine = engine;
        this.script = script;
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("ctx", context);
        this.compiledScript = ((Compilable) engine).compile(script);
    }

    public Object eval() throws ScriptException {
        return engine.eval(script);
    }

    public Object evalCompiled() throws ScriptException {
        return compiledScript.eval();
    }

    public void compile() throws ScriptException {
        ((Compilable) engine).compile(script);
    }
}
