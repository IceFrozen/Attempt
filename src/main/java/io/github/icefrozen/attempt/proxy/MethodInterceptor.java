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

import io.github.icefrozen.attempt.exception.TargetMethodException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 方法拦截器
 *
 * @author Jason Lee
 */
public interface MethodInterceptor {
    /**
     * 拦截
     *
     * @param targetMethod 目标方法
     * @return 返回值
     */
    Object intercept(TargetMethod targetMethod);

    /**
     * 执行目标方法
     *
     * @return 方法拦截器
     */
    static MethodInterceptor invokeTargetMethod() {
        return TargetMethod::invokeWithOriginalParams;
    }

    /**
     * 拦截参数
     *
     * @param interceptor 参数拦截器
     */
    static MethodInterceptor interceptParameters(ParametersInterceptor interceptor) {
        return targetMethod -> {
            Object[] params = targetMethod.getParams();
            return targetMethod.invoke(interceptor.intercept(params));
        };
    }

    /**
     * 拦截返回值
     *
     * @param interceptor 返回值拦截器
     */
    static MethodInterceptor interceptReturnValue(ReturnValueInterceptor interceptor) {
        return targetMethod -> interceptor.intercept(targetMethod.invokeWithOriginalParams());
    }

    /**
     * 拦截异常
     *
     * @param interceptor 异常拦截器
     */
    static MethodInterceptor interceptException(ExceptionInterceptor interceptor) {
        return targetMethod -> {
            try {
                return targetMethod.invokeWithOriginalParams();
            } catch (TargetMethodException e) {
                return interceptor.intercept(e.getTargetException());
            }
        };
    }

    /**
     * 将目标类的部分方法委托到代理类
     *
     * @param proxy 代理类
     */
    static MethodInterceptor delegateTo(Object proxy) {
        return targetMethod -> {
            Object[] params = targetMethod.getParams();
            MethodSignature signature = targetMethod.getSignature();
            Method m = null;
            try {
                m = proxy.getClass().getDeclaredMethod(signature.getName(), signature.getParameterTypes());
                m.setAccessible(true);
                return m.invoke(proxy, params);
            }
            // 代理对象中没有该方法，则调用目标对象方法
            catch (NoSuchMethodException | IllegalAccessException e) {
                return targetMethod.invokeWithOriginalParams();
            }
            // 代理对象的方法抛出异常
            catch (InvocationTargetException e) {
                throw new TargetMethodException(e.getCause(), m);
            }
        };
    }

    /**
     * 指定拦截条件
     *
     * @param matcher 方法匹配器
     */
    default MethodInterceptor when(MethodMatcher matcher) {
        return targetMethod -> {
            MethodSignature signature = targetMethod.getSignature();
            if (matcher.match(signature)) {
                return this.intercept(targetMethod);
            }
            return targetMethod.invokeWithOriginalParams();
        };
    }

    /**
     * 多重代理
     *
     * @param interceptor 方法拦截器
     */
    default MethodInterceptor then(MethodInterceptor interceptor) {
        return targetMethod -> {
            return interceptor.intercept(new TargetMethod(targetMethod.getSignature(), (Invokable) params -> {
                return this.intercept(new TargetMethod(
                        targetMethod.getSignature(),
                        targetMethod.getInvokable(),
                        params));
            }, targetMethod.getParams()));
        };
    }
}
