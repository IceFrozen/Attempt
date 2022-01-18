package com.github.IceFrozen.attempt.invoker;


@FunctionalInterface
public interface ThrowSafetyFunctionInvokerSupplier<T> extends ThrowSafetyFunctionInvoker<T> {
    T get() throws Exception;
}

