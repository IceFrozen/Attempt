package io.github.icefrozen.attempt.exception;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class BeanInstantiationException extends BaseException{
    private final Class<?> beanClass;
    private final Constructor<?> constructor;
    private final Method constructingMethod;


    /**
     * Create a new BeanInstantiationException.
     * @param beanClass the offending bean class
     * @param msg the detail message
     */
    public BeanInstantiationException(Class<?> beanClass, String msg) {
        this(beanClass, msg, null);
    }


    public BeanInstantiationException(Class<?> beanClass, String msg, Throwable cause) {
        super("Failed to instantiate [" + beanClass.getName() + "]: " + msg, cause);
        this.beanClass = beanClass;
        this.constructor = null;
        this.constructingMethod = null;
    }


    public BeanInstantiationException(Constructor<?> constructor, String msg, Throwable cause) {
        super("Failed to instantiate [" + constructor.getDeclaringClass().getName() + "]: " + msg, cause);
        this.beanClass = constructor.getDeclaringClass();
        this.constructor = constructor;
        this.constructingMethod = null;
    }


    public BeanInstantiationException(Method constructingMethod, String msg, Throwable cause) {
        super("Failed to instantiate [" + constructingMethod.getReturnType().getName() + "]: " + msg, cause);
        this.beanClass = constructingMethod.getReturnType();
        this.constructor = null;
        this.constructingMethod = constructingMethod;
    }



    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    public Constructor<?> getConstructor() {
        return this.constructor;
    }


    public Method getConstructingMethod() {
        return this.constructingMethod;
    }
}
