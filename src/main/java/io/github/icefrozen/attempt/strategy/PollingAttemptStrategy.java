package io.github.icefrozen.attempt.strategy;

import io.github.icefrozen.attempt.*;
import io.github.icefrozen.attempt.proxy.TargetMethod;

/**
 * 重试轮询策略
 */
public class PollingAttemptStrategy extends RetryAttemptStrategy {
    AttemptRecord record = new AttemptRecord();

    public PollingAttemptStrategy(String strategyName, BaseAttemptPropertyViewer<BaseAttemptPropertyViewer<?>> properties) {
        super(strategyName, properties);
    }
    public PollingAttemptStrategy(String strategyName) {
        super(strategyName);
    }

    @Override
    public boolean isEnd(AttemptContext context) {
        boolean isMaxPollEnd = false;
        boolean isRetryEnd = false;
        if (properties.retryMax() > 0) {
            isRetryEnd = record.getExceptionCount() >= properties.retryMax();
        }
        if (properties.maxPollCount() > 0) {
            isMaxPollEnd = record.getExecuteCount() >= properties.maxPollCount();
        }
        return isRetryEnd || isMaxPollEnd;
    }

    @Override
    public boolean back(AttemptResult record, AttemptContext context) {
        if(!record.isSuccess()) {
            // 如果轮询之后，出现异常，则进入下一个请求周期，状态为holding状态
            this.status = AttemptStatus.HOLDING;
            this.backOff(record, context);
            return false;
        }
        // 如果请求正常，由是期望的结果，则直接退出返回
        boolean expect = properties.endPoint().apply(context);
        // 如果成功，则清空错误历史
        context.reset();
        if (expect) {
            return true;
        }
        // 如果请求成功， 不是期望的结果，则进入下一个轮询，轮询次数+1
        this.status = AttemptStatus.PROGRESS;
        this.backOff(record, context);
        return false;
    }

    @Override
    public void invokeExecutorEnd(
            AttemptExecutor executor, AttemptContext context) {
        super.invokeExecutorEnd(executor, context);
        this.record.reset();
    }

    @Override
    public void invokeAfterMethod(
            AttemptExecutor executor, AttemptContext context, TargetMethod method, AttemptResult record) {
        this.record.incrementExecuteCount();
        if (!record.isSuccess()) {
            this.record.incrementExceptionCount();
        }
    }

    public void backOff(AttemptResult record, AttemptContext context) {
        super.exceptionHandler(record, context);
        if (this.properties.backOff() != null) {
            this.properties.backOff().backOff();
        }
    }
}
