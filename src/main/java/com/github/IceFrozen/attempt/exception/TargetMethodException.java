package com.github.IceFrozen.attempt.exception;

import java.lang.reflect.Method;

public class TargetMethodException extends ProxyException implements OriginExceptionWrapper {
    private final Throwable targetException;

    public TargetMethodException(Throwable e, Method method) {
        super("Exception from target method: " + method, e);
        this.targetException = e;
    }

    public Throwable getTargetException() {
        return targetException;
    }

    @Override
    public Throwable getOriginException() {
        return this.targetException;
    }
}
