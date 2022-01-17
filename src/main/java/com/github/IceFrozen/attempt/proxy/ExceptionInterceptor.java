package com.github.IceFrozen.attempt.proxy;

/**
 * 异常拦截器
 *
 * @author Jason Lee
 */
public interface ExceptionInterceptor {
    Object intercept(Throwable e);
}
