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

import java.lang.reflect.Method;

/**
 * 目标方法
 */
public class TargetMethod {
    private final MethodSignature signature;
    private final Invokable invokable;
    private final Object[] params;

    /**
     * 创建目标方法
     *
     * @param signature 方法签名
     * @param invokable 调用器
     * @param params    原始参数
     */
    public TargetMethod(MethodSignature signature, Invokable invokable, Object[] params) {
        this.signature = signature;
        this.invokable = invokable;
        this.params = params;
    }

    /**
     * 获取方法签名
     *
     * @return 方法签名
     */
    public MethodSignature getSignature() {
        return signature;
    }

    /**
     * 获取方法调用器
     *
     * @return 调用器
     */
    public Invokable getInvokable() {
        return invokable;
    }

    /**
     * 获取原始参数
     *
     * @return 原始参数数组
     */
    public Object[] getParams() {
        return params;
    }

    /**
     * 使用原始参数调用目标方法
     *
     * @return 返回值
     */
    public Object invokeWithOriginalParams() {
        return invokable.invoke(params);
    }

    /**
     * 使用特定参数调用原始方法
     *
     * @param params 参数
     * @return 返回值
     */
    public Object invoke(Object... params) {
        return invokable.invoke(params);
    }

    /**
     * 获取Method对象
     * @return Method对象
     */
    public Method getMethod() {
        return signature.getMethod();
    }
}
