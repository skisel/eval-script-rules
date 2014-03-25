/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.skisel.benchmarks;

import com.skisel.Context;
import org.openjdk.jmh.annotations.*;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class BusinessRulesEvalBenchmark {

    private RuleEvaluator ruleEvaluator;

    @Param({"JavaScript", "groovy", "scala"})
    private String engine;

    private static HashMap<String, String> engineToScriptMap = new HashMap<String, String>();

    static {
        engineToScriptMap.put("scala", "import com.skisel.Context; val c = ctx.asInstanceOf[Context]; c.readDouble(\"a\")+ c.readDouble(\"b\")");
        engineToScriptMap.put("JavaScript", "ctx.read('a') + ctx.read('b');");
        engineToScriptMap.put("groovy", "ctx.read('a') + ctx.read('b');");
    }

    @Setup
    public void setup() throws ScriptException {
        System.setProperty("scala.usejavacp", "true");
        ScriptEngineManager factory = new ScriptEngineManager();
        Context context = new Context();
        context.write("a", 1.0);
        context.write("b", 2.0);
        ruleEvaluator = new RuleEvaluator(factory.getEngineByName(engine), engineToScriptMap.get(engine), context);
    }

    @GenerateMicroBenchmark
    public void eval() throws ScriptException {
        ruleEvaluator.eval();
    }

    @GenerateMicroBenchmark
    public void evalCompiled() throws ScriptException {
        ruleEvaluator.evalCompiled();
    }

    @GenerateMicroBenchmark
    public void compile() throws ScriptException {
        ruleEvaluator.compile();
    }

}
