package io.github.icefrozen.attempt.invoker;


@FunctionalInterface
public interface ThrowSafetyFunctionInvokerSupplier<T> extends ThrowSafetyFunctionInvoker<T> {
    T get() throws Exception;
}

