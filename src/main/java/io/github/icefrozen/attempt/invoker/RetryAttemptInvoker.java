package io.github.icefrozen.attempt.invoker;

import io.github.icefrozen.attempt.strategy.RetryAttemptStrategy;

public class RetryAttemptInvoker<T> extends AttemptInvoker<T> {
    public RetryAttemptInvoker(ThrowSafetyFunctionInvoker<T> action) {
        super(action);
        super.retryMax(3);
        super.strategy(RetryAttemptStrategy.class);
    }
}
