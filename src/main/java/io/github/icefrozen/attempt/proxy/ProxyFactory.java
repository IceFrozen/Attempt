package io.github.icefrozen.attempt.proxy;

import io.github.icefrozen.attempt.AttemptException;

public class ProxyFactory {

    public static <T> T proxy(Object target, Object interceptor) {
        if (interceptor instanceof MethodInterceptor) {
            return proxyWitheProxyUtil(target, (MethodInterceptor) interceptor);
        }

        throw new AttemptException("ProxyFactory can't find proxy ");
    }

    public static <T> T proxyWitheProxyUtil(Object target, MethodInterceptor interceptor) {
        return ProxyUtils.proxy(target, interceptor);
    }
}
