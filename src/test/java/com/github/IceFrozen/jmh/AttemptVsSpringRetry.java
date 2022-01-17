package com.github.IceFrozen.jmh;

import com.github.IceFrozen.attempt.AttemptBuilder;
import com.github.IceFrozen.attempt.testBean.ProxyDemo2;

import org.openjdk.jmh.annotations.*;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.NoBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Threads(3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@State(Scope.Benchmark)
public class AttemptVsSpringRetry {
    ProxyDemo2 proxyDemoSpring = new ProxyDemo2();
    ProxyDemo2 proxyDemoAttempt = new ProxyDemo2();
    RetryTemplate retryTemplate;

    @Benchmark
    public void testAttempt() {
        AttemptBuilder.retry(() -> proxyDemoAttempt.errorMethod()).noThrow();
    }
    @Benchmark
    public void testSpringRetry() throws Throwable {
        retryTemplate.execute((RetryCallback<Object, Throwable>) retryContext -> proxyDemoSpring.errorMethod(), retryContext -> null);
    }


    @Setup
    public void build() {
        retryTemplate = new RetryTemplate();
        BackOffPolicy backOffPolicy =   new NoBackOffPolicy();
        retryTemplate.setBackOffPolicy(backOffPolicy);
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);
    }

}
