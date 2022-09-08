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

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

/**
 * 动态代理工具类
 *
 * @author Jason Lee
 */
public class ProxyUtils {
    /**
     * 创建代理对象
     *
     * @param target      目标对象
     * @param interceptor 方法拦截器
     * @param <T>         返回类型
     * @return 被增强的代理对象
     */
    public static <T> T proxy(Object target, MethodInterceptor interceptor) {
        // final类使用jdk代理
        if (Modifier.isFinal(target.getClass().getModifiers())) {
            return proxyByJdk(target, interceptor);
        }
        // 没有接口的类使用cglib代理
        else if (target.getClass().getInterfaces().length == 0) {
            return proxyByCglib(target, interceptor);
        }
        // 其他情况使用jdk代理
        else {
            return proxyByJdk(target, interceptor);
        }
    }

    /**
     * 动态实现接口
     *
     * @param interfaceType 接口类型
     * @param interceptor   方法拦截器
     * @param <T>           返回类型
     * @return 动态生成的接口实现类
     */
    public static <T> T implement(Class<T> interfaceType, MethodInterceptor interceptor) {
        return interfaceType.cast(Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class<?>[]{interfaceType}, (proxy, method, args) -> {
            return interceptor.intercept(new TargetMethod(MethodSignature.of(method), Invokable.of(proxy, method, null), args));
        }));
    }

    /**
     * 动态生成子类
     *
     * @param parentType  父类
     * @param interceptor 方法拦截器
     * @param <T>         返回类型
     * @return 动态生成的子类
     */
    public static <T> T extend(Class<T> parentType, MethodInterceptor interceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(parentType);
        enhancer.setCallback((InvocationHandler) (proxy, method, args) -> {
            return interceptor.intercept(new TargetMethod(MethodSignature.of(method), Invokable.of(proxy, method, null), args));
        });
        return parentType.cast(enhancer.create());
    }

    /**
     * 使用jdk动态代理
     */
    @SuppressWarnings("unchecked")
    public static <T> T proxyByJdk(Object target, MethodInterceptor interceptor) {
        return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), (proxy, method, args) -> {
            Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
            return interceptor.intercept(new TargetMethod(MethodSignature.of(targetMethod), Invokable.of(proxy, targetMethod, target), args));
        });
    }

    /**
     * 使用cglib动态代理
     */
    @SuppressWarnings("unchecked")
    public static <T> T proxyByCglib(Object target, MethodInterceptor interceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setInterfaces(new Class[]{CglibProxy.class});
        enhancer.setCallback((InvocationHandler) (proxy, method, args) ->
                interceptor.intercept(
                        new TargetMethod(MethodSignature.of(method), Invokable.of(proxy, method, target), args)));
        return (T) enhancer.create();
    }

    /**
     * 用于判断 该类是否是代理类
     * @param proxyClass
     * @return
     */
    public static boolean isProxy(Class<?> proxyClass) {
        return Proxy.isProxyClass(proxyClass) ||  CglibProxy.class.isAssignableFrom(proxyClass);
    }
}
