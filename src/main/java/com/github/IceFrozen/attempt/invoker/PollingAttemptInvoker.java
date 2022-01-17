package com.github.IceFrozen.attempt.invoker;

import com.github.IceFrozen.attempt.strategy.PollingAttemptStrategy;

import java.util.function.Supplier;

public class PollingAttemptInvoker<T> extends AttemptInvoker<T> {
    public PollingAttemptInvoker(Supplier<T> action) {
        super(action);
        super.retryMax(-1);
        super.strategy(PollingAttemptStrategy.class);
    }
}
