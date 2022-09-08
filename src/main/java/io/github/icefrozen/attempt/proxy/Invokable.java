/*
 * MIT License
 *
 * Copyright (c) 2022 Jason Lee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.github.icefrozen.attempt.proxy;

import io.github.icefrozen.attempt.exception.NotImplementedException;
import io.github.icefrozen.attempt.exception.ProxyException;
import io.github.icefrozen.attempt.exception.TargetMethodException;

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
