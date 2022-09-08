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

package io.github.icefrozen.attempt.invoker;

import io.github.icefrozen.attempt.*;
import io.github.icefrozen.attempt.exception.SneakyExceptionUtil;
import io.github.icefrozen.attempt.listeners.InvokeListener;
import io.github.icefrozen.attempt.proxy.TargetMethod;

public class AttemptInvoker<T> extends BaseAttemptPropertyViewer<AttemptInvoker<T>> implements Invoker<T>, InvokeListener {
    private ThrowSafetyFunctionInvoker<T> action;
    Attempt<ThrowSafetyFunctionInvoker<T>> attempt;
    // 结果缓存器
    AttemptResultContainer container;

    public AttemptInvoker(ThrowSafetyFunctionInvoker<T> action) {
        this.action = action;
        super.retryMax(1);
        container = new AttemptResultContainer(super.retryMax() == -1 ? 1000 : super.retryMax());
    }

    public T exec() {
        try {
            Attempt<ThrowSafetyFunctionInvoker<T>> attempt = this.getAttempt();
            ThrowSafetyFunctionInvoker<T> proxyObject = attempt.getProxyObject();
            return ThrowSafetyFunctionInvoker.invoke(proxyObject);
        } catch (Throwable e) {
            if (this.defaultValue() != null) {
                return (T) this.defaultValue().getRetValue();
            } else {
                SneakyExceptionUtil.sneakyThrow(e);
            }
        }
        return null;
    }

    public T exec(T defaultValue) {
        this.defaultValue(defaultValue);
        return exec();
    }

    public Attempt<ThrowSafetyFunctionInvoker<T>> getAttempt() {
        if (this.attempt == null) {
            this.addListener(this);
            AttemptExecutor executor = AttemptBuilderFactory.generateExecutor(this);
            attempt = new Attempt<>(executor, action);
        }
        return this.attempt;
    }

    @Override
    public void invokeAfterMethod(AttemptExecutor executor, AttemptContext context, TargetMethod method, AttemptResult record) {
        this.container.record(record);
    }

    public AttemptResult getResult() {
        this.exec();
        return this.container.getResult();
    }

    public AttemptResultContainer getContainers() {
        return this.container;
    }
}
