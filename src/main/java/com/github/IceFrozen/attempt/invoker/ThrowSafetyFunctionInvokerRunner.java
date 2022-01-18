package com.github.IceFrozen.attempt.invoker;
@FunctionalInterface
public interface ThrowSafetyFunctionInvokerRunner<Void> extends ThrowSafetyFunctionInvoker<Void> {
    void run() throws Exception;
}
