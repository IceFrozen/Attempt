package io.github.icefrozen.attempt.invoker;

import io.github.icefrozen.attempt.strategy.PollingAttemptStrategy;

public class PollingAttemptInvoker<T> extends AttemptInvoker<T> {
    public PollingAttemptInvoker(ThrowSafetyFunctionInvoker<T> action) {
        super(action);
        super.retryMax(-1);
        super.strategy(PollingAttemptStrategy.class);
    }
}
