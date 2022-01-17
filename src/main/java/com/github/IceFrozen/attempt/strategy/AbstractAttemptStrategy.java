package com.github.IceFrozen.attempt.strategy;

import com.github.IceFrozen.attempt.AttemptContext;
import com.github.IceFrozen.attempt.AttemptResult;
import com.github.IceFrozen.attempt.AttemptStatus;
import com.github.IceFrozen.attempt.BaseAttemptPropertyViewer;


public abstract class AbstractAttemptStrategy implements AttemptStrategy {
    final BaseAttemptPropertyViewer<? extends BaseAttemptPropertyViewer<?>> properties;
    protected final String strategyName;                      // 策略名字
    protected AttemptStatus status;

    public AbstractAttemptStrategy(String strategyName, BaseAttemptPropertyViewer<?> properties) {
        this.properties = properties;
        this.strategyName = strategyName;
        this.status = AttemptStatus.PROGRESS;
    }

    public BaseAttemptPropertyViewer<? extends BaseAttemptPropertyViewer<?>> getProperties() {
        return properties;
    }

    @Override
    public boolean isEnd(AttemptContext context) {
        return context.getExecuteCount() >= properties.retryMax();
    }

    @Override
    public boolean back(AttemptResult record, AttemptContext context) {
        if (this.properties.backOff() != null) {
            this.properties.backOff().backOff();
        }
        return isEnd(context);
    }

    @Override
    public String name() {
        return this.strategyName;
    }

    @Override
    public BaseAttemptPropertyViewer<? extends BaseAttemptPropertyViewer<?>> properties() {
        return this.properties;
    }

    @Override
    public AttemptStatus status() {
        return this.status;
    }

    @Override
    public AttemptStatus status(AttemptStatus status) {
        return this.status = status;
    }
}
