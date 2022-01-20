package io.github.icefrozen.attempt.listeners;


import io.github.icefrozen.attempt.AttemptContext;
import io.github.icefrozen.attempt.AttemptExecutor;

/**
 * 执行器监听器，如果实行该方法内抛出异常，
 * 则不会被重试策略捕获，而是直接抛出
 */
public interface ExecutorListener extends AttemptListener {
    // 执行器开始前调用
    default void invokeExecutorStart(AttemptExecutor executor, AttemptContext context) {};

    //执行器异常时执行
    default void invokeExecutorException(AttemptExecutor executor, AttemptContext context, Exception e) {};

    // 执行器结束后触发
    default void invokeExecutorEnd(AttemptExecutor executor, AttemptContext context) {};
}
