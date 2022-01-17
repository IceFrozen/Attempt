package com.github.IceFrozen.attempt.proxy;

import com.github.IceFrozen.attempt.exception.NotImplementedException;
import com.github.IceFrozen.attempt.exception.ProxyException;
import com.github.IceFrozen.attempt.exception.TargetMethodException;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 对一个可调用方法的封装
 */
public interface Invokable {
    /**
     * 调用方法
     *
     * @param params 参数
     * @return 返回值
     */
    Object invoke(Object... params);

    /**
     * 创建一个Invokable
     *
     * @param method 方法对象
     * @param target 实例
     */
    static Invokable of(Object proxy, Method method, Object target) {
        return params -> {
            try {
                if (target == null) {
                    if (!method.isDefault()) {
                        throw new NotImplementedException(method);
                    }
                    Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                    constructor.setAccessible(true);
                    Class<?> declaringClass = method.getDeclaringClass();
                    int allModes = MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE;
                    return constructor.newInstance(declaringClass, allModes)
                            .unreflectSpecial(method, declaringClass)
                            .bindTo(proxy)
                            .invokeWithArguments(params);
                }
                method.setAccessible(true);
                return method.invoke(target, params);
            } catch (InvocationTargetException e) {
                throw new TargetMethodException(e.getTargetException(), method);
            } catch (IllegalAccessException e) {
                throw new ProxyException("Cannot invoke target method: " + method, e);
            } catch (NotImplementedException e) {
                throw e;
            } catch (Throwable e) {
                throw new ProxyException(e);
            }
        };
    }
}
