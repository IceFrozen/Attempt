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

import io.github.icefrozen.attempt.invoker.PollingAttemptInvoker;
import io.github.icefrozen.attempt.invoker.RetryAttemptInvoker;
import io.github.icefrozen.attempt.invoker.ThrowSafetyFunctionInvokerRunner;
import io.github.icefrozen.attempt.invoker.ThrowSafetyFunctionInvokerSupplier;
import io.github.icefrozen.attempt.strategy.PollingAttemptStrategy;
import io.github.icefrozen.attempt.strategy.RetryAttemptStrategy;

/**
 * AttemptBuilder builds the builder of Attempt
 *
 * @author Jaosn Lee
 */
public class AttemptBuilder {

    public static class Polling<T> extends
            BaseAttemptPropertyViewer<Polling<T>> {
        private final T originInstance;
        private Attempt<T> attempt;

        public Polling(T t) {
            super.strategy(PollingAttemptStrategy.class);
            super.retryMax(-1);
            originInstance = t;
        }

        public Attempt<T> attempt() {
            if (this.attempt == null) {
                this.build();
            }
            return this.attempt;
        }

        public T build() {
            if (this.endPoint() == null) {
                throw new AttemptException("Polling must set endpoint!");
            }

            if (!PollingAttemptStrategy.class.isAssignableFrom(this.strategyClass())) {
                throw new AttemptException("strategyClass must be PollingStrategy!");
            }

            if (this.attempt == null) {
                AttemptExecutor executor = AttemptBuilderFactory
                        .generateExecutor(this);
                attempt = new Attempt<>(executor, originInstance);
            }
            return attempt.getProxyObject();
        }
    }


    /**
     * 构建Retry对象 对象
     *
     * @param <T> 返回值
     */
    public static class Retry<T> extends
            BaseAttemptPropertyViewer<Retry<T>> {
        private final T originInstance;
        private Attempt<T> attempt;

        public Retry(T t) {
            originInstance = t;
        }

        public Attempt<T> attempt() {
            if (this.attempt == null) {
                this.build();
            }
            return this.attempt;
        }

        public T build() {
            if (this.attempt == null) {
                if (!RetryAttemptStrategy.class.isAssignableFrom(this.strategyClass())) {
                    throw new AttemptException("strategyClass must be PollingStrategy!");
                }
                AttemptExecutor executor = AttemptBuilderFactory
                        .generateExecutor(this);
                attempt = new Attempt<>(executor, originInstance);
            }
            return attempt.getProxyObject();
        }
    }

    public static <T> RetryAttemptInvoker<T> retry(ThrowSafetyFunctionInvokerSupplier<T> supplier) {
        return new RetryAttemptInvoker<T>(supplier);
    }

    public static <Void> RetryAttemptInvoker<Void> retry(ThrowSafetyFunctionInvokerRunner<Void> runnable) {
        return new RetryAttemptInvoker<Void>(runnable);
    }

    public static <T> PollingAttemptInvoker<T> polling(ThrowSafetyFunctionInvokerSupplier<T> supplier) {
        return new PollingAttemptInvoker<T>(supplier);
    }

    public static <Void> PollingAttemptInvoker<Void> polling(ThrowSafetyFunctionInvokerRunner<Void> runnable) {
        return new PollingAttemptInvoker<Void>(runnable);
    }
}