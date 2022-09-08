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

import io.github.icefrozen.attempt.listeners.AttemptStrategyListener;
import io.github.icefrozen.attempt.listeners.AttemptTimeoutListener;
import io.github.icefrozen.attempt.strategy.AttemptStrategy;
import io.github.icefrozen.attempt.util.BeanUtils;

import java.lang.reflect.Constructor;

/**
 * Executor builder
 *
 * @author Jaosn Lee
 */
public class AttemptBuilderFactory {
    public static AttemptExecutor generateExecutor(BaseAttemptPropertyViewer<?> viewer) {
        AttemptExecutor attemptExecutor;
        try {
            if (viewer.context() == null) {
                viewer.context(BeanUtils.instantiateClass(viewer.getContextClass()));
            }
            if (viewer.backOff() == null) {
                viewer.backOff(BeanUtils.instantiateClass(viewer.getBackOffClass()));
            }
            if (viewer.name() == null) {
                viewer.name(viewer.strategyClass().getName() + "[" + viewer.retryMax() + "]@");
            }
            if (viewer.strategy() == null) {
                Constructor<? extends AttemptStrategy> constructor = BeanUtils.accessibleConstructor(viewer.strategyClass(), String.class, BaseAttemptPropertyViewer.class);
                viewer.strategy(BeanUtils.instantiateClass(constructor, viewer.name(), viewer));
                if (viewer.exceptionRecord().isEmpty()) {
                    viewer.registerExceptionRetryTime(Exception.class, viewer.retryMax() <= 0 ? 3 : viewer.retryMax());
                }
            }
            viewer.addListener(new AttemptStrategyListener());
            if (viewer.timeout() > 0) {
                viewer.addListener(new AttemptTimeoutListener());
            }
            Constructor<AttemptExecutor> constructor = BeanUtils.accessibleConstructor(AttemptExecutor.class, AttemptContext.class, AttemptStrategy.class);
            attemptExecutor = BeanUtils.instantiateClass(constructor, viewer.context(), viewer.strategy());
            attemptExecutor.setDefaultValue(viewer.defaultValue());
            attemptExecutor.setListeners(viewer.getListeners());
        } catch (Exception e) {
            throw new AttemptException(e.getCause(), "RetryBuilder builder exception:{}", e.getMessage());
        }
        return attemptExecutor;

    }
}
