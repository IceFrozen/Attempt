package io.github.icefrozen.attempt.listeners;

import io.github.icefrozen.attempt.AttemptContext;
import io.github.icefrozen.attempt.AttemptExecutor;
import io.github.icefrozen.attempt.AttemptResult;
import io.github.icefrozen.attempt.proxy.TargetMethod;

/**
 * 方法调用监听器，该方法中如果抛出异常，则会执行执行器默认策略
 */
public interface InvokeListener extends AttemptListener {
    // 每一次实际调用前调用
    default void invokeBeforeMethod(AttemptExecutor executor, AttemptContext context, TargetMethod method) {};

    // 每一次实际调用后调用
    default void invokeAfterMethod(AttemptExecutor executor, AttemptContext context, TargetMethod method,
            AttemptResult record) {};

    // 每一次调用异常后调用
    default void invokeOriginException(AttemptExecutor executor, AttemptContext context, TargetMethod method,
            Throwable e) {};
}