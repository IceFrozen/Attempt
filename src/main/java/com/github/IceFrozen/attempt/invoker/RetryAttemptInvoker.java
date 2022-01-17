package com.github.IceFrozen.attempt.invoker;

import com.github.IceFrozen.attempt.strategy.RetryAttemptStrategy;

import java.util.function.Supplier;

public class RetryAttemptInvoker<T> extends AttemptInvoker<T> {
    public RetryAttemptInvoker(Supplier<T> action) {
        super(action);
        super.retryMax(3);
        super.strategy(RetryAttemptStrategy.class);
    }
}
