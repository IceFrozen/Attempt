package com.github.IceFrozen.jmh;

import org.junit.Test;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class JMHTest {

    @Test //If you want to do a performance test open this
    public void testAttempt() throws RunnerException {
        Options options = new OptionsBuilder()
                .include(AttemptVsSpringRetry.class.getSimpleName())
                .result("/tmp/attempt.json")
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(options).run();
    }
}
