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
