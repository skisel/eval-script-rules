package com.skisel.benchmarks;

import org.openjdk.jmh.output.results.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * User: sergeykisel
 * Date: 18.03.14
 * Time: 9:41
 */
public class Main {
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*Benchmark.*")
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(20)
                .result("result.csv")
                .threads(4)
                .resultFormat(ResultFormatType.NONE)
                .build();

        new Runner(opt).run();
    }
}
