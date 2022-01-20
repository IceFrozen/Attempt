package io.github.icefrozen.attempt.listeners;

import io.github.icefrozen.attempt.AttemptContext;
import io.github.icefrozen.attempt.AttemptExecutor;
import io.github.icefrozen.attempt.AttemptResult;
import io.github.icefrozen.attempt.exception.AttemptTimeoutException;
import io.github.icefrozen.attempt.proxy.TargetMethod;
import io.github.icefrozen.attempt.strategy.AttemptStrategy;

public class AttemptTimeoutListener implements ExecutorListener, InvokeListener {
    private Long startTime;
    private Long timeout;
    private Long endTime;
    private AttemptStrategy strategy;

    @Override
    public void invokeExecutorStart(
            AttemptExecutor executor, AttemptContext context) {
        startTime = executor.getStartTime();
        timeout = executor.getStrategy().properties().timeout();
        endTime = startTime + timeout;
        strategy = executor.getStrategy();
    }

    @Override
    public void invokeBeforeMethod(
            AttemptExecutor executor, AttemptContext context, TargetMethod method) {
        Long timePoint = System.currentTimeMillis();
        if (isTimeout(timePoint)) {
            throw new AttemptTimeoutException("the attempt {} is timeout: last time:{} ms:", strategy.name(), timePoint - startTime);
        }
    }

    @Override
    public void invokeAfterMethod(
            AttemptExecutor executor, AttemptContext context, TargetMethod method, AttemptResult record) {
        Long timePoint = record.getEndTime();
        if (isTimeout(timePoint)) {
            throw new AttemptTimeoutException("the attempt {} is timeout: last time:{} ms:", strategy.name(), timePoint - startTime);
        }
    }

    @Override
    public void invokeOriginException(
            AttemptExecutor executor, AttemptContext context, TargetMethod method, Throwable e) {

    }

    @Override
    public void invokeExecutorException(AttemptExecutor executor, AttemptContext context, Exception e) {

    }

    @Override
    public void invokeExecutorEnd(
            AttemptExecutor executor, AttemptContext context) {
        endTime = 0L;
    }


    public boolean isTimeout(Long timePoint) {
        return timePoint > endTime;
    }
}
