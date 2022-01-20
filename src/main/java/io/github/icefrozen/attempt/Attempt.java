package io.github.icefrozen.attempt;

import io.github.icefrozen.attempt.proxy.ProxyFactory;

/**
 * The Attempt is the intent for an action, transform a logical action into a simple call.
 *
 * @param <T> The end result you want to get.
 * @author Jason Lee
 */
public class Attempt<T> {
    private AttemptExecutor executor;
    private T originObject;
    private T proxyObject;

    public Attempt(AttemptExecutor executor, T originObject, T proxyObject) {
        this.executor = executor;
        this.originObject = originObject;
        this.proxyObject = proxyObject;
    }

    public Attempt(AttemptExecutor executor, T originObject) {
        this(executor, originObject, null);
    }

    public AttemptExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(AttemptExecutor executor) {
        this.executor = executor;
    }

    public T getOriginObject() {
        return originObject;
    }

    public void setOriginObject(T originObject) {
        this.originObject = originObject;
    }

    public T getProxyObject() {
        if (proxyObject != null) {
            return proxyObject;
        }
        proxyObject = ProxyFactory.proxy(originObject, executor);
        return proxyObject;
    }

    public void setProxyObject(T proxyObject) {
        this.proxyObject = proxyObject;
    }
}
