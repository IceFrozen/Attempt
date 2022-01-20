package io.github.icefrozen.attempt.listeners;

import io.github.icefrozen.attempt.AttemptContext;
import io.github.icefrozen.attempt.AttemptExecutor;
import io.github.icefrozen.attempt.AttemptResult;
import io.github.icefrozen.attempt.proxy.TargetMethod;
import io.github.icefrozen.attempt.strategy.AttemptStrategy;

/**
 * 策略传递监听器
 */
public class AttemptStrategyListener implements ExecutorListener, InvokeListener {
    @Override
    public void invokeExecutorStart(AttemptExecutor executor, AttemptContext context) {
        AttemptStrategy strategy = executor.getStrategy();
        if (strategy instanceof ExecutorListener) {
            ((ExecutorListener) strategy).invokeExecutorStart(executor, executor.getContext());
        }
    }

    @Override
    public void invokeExecutorException(AttemptExecutor executor, AttemptContext context, Exception e) {
        AttemptStrategy strategy = executor.getStrategy();
        if (strategy instanceof ExecutorListener) {
            ((ExecutorListener) strategy).invokeExecutorException(executor, executor.getContext(), e);
        }
    }

    @Override
    public void invokeExecutorEnd(AttemptExecutor executor, AttemptContext context) {
        AttemptStrategy strategy = executor.getStrategy();
        if (strategy instanceof ExecutorListener) {
            ((ExecutorListener) strategy).invokeExecutorEnd(executor, executor.getContext());
        }
    }

    @Override
    public void invokeBeforeMethod(AttemptExecutor executor, AttemptContext context, TargetMethod method) {
        AttemptStrategy strategy = executor.getStrategy();
        if (strategy instanceof InvokeListener) {
            ((InvokeListener) strategy).invokeBeforeMethod(executor, executor.getContext(), method);
        }
    }


    @Override
    public void invokeAfterMethod(AttemptExecutor executor, AttemptContext context, TargetMethod method, AttemptResult record) {
        AttemptStrategy strategy = executor.getStrategy();
        if (strategy instanceof InvokeListener) {
            ((InvokeListener) strategy).invokeAfterMethod(executor, executor.getContext(), method, record);
        }
    }

    @Override
    public void invokeOriginException(AttemptExecutor executor, AttemptContext context, TargetMethod method, Throwable e) {
        AttemptStrategy strategy = executor.getStrategy();
        if (strategy instanceof InvokeListener) {
            ((InvokeListener) strategy).invokeOriginException(executor, executor.getContext(), method, e);
        }
    }
}
