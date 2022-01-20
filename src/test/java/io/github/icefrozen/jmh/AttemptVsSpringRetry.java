package io.github.icefrozen.jmh;

import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import io.github.icefrozen.attempt.AttemptBuilder;
import io.github.icefrozen.attempt.testBean.ProxyDemo2;

import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.NoBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
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
    ProxyDemo2 proxyDemoGuava = new ProxyDemo2();
    RetryTemplate retryTemplate;
    Retryer<Integer> retryer;

    @Benchmark
    public void testAttempt() {
        AttemptBuilder.retry(() -> proxyDemoAttempt.errorMethod()).retryMax(300).noThrow();
    }
    @Benchmark
    public void testSpringRetry() throws Throwable {
        retryTemplate.execute((RetryCallback<Object, Throwable>) retryContext -> proxyDemoSpring.errorMethod(), retryContext -> null);
    }

    @Benchmark
    public void testGuavaRetry() throws Throwable {
        try {
            retryer.call(() -> proxyDemoGuava.errorMethod());
        } catch (Exception e) {

        }
    }

    @Setup
    public void build() throws Throwable {
        retryTemplate = new RetryTemplate();
        BackOffPolicy backOffPolicy =   new NoBackOffPolicy();
        retryTemplate.setBackOffPolicy(backOffPolicy);
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(300);

        retryTemplate.setRetryPolicy(retryPolicy);
        retryer = RetryerBuilder.<Integer>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withStopStrategy(StopStrategies.stopAfterAttempt(300))
                .build();
    }

}
