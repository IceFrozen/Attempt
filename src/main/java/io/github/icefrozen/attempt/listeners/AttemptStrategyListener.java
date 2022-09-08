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

package io.github.icefrozen.attempt.listeners;

import io.github.icefrozen.attempt.AttemptContext;
import io.github.icefrozen.attempt.AttemptExecutor;
import io.github.icefrozen.attempt.AttemptResult;
import io.github.icefrozen.attempt.proxy.TargetMethod;
import io.github.icefrozen.attempt.strategy.AttemptStrategy;

/**
 * 策略传递监听器
 */
public class AttemptStrategyListener implements ExecutorListener, InvokeListener {
    @Override
    public void invokeExecutorStart(AttemptExecutor executor, AttemptContext context) {
        AttemptStrategy strategy = executor.getStrategy();
        if (strategy instanceof ExecutorListener) {
            ((ExecutorListener) strategy).invokeExecutorStart(executor, executor.getContext());
        }
    }

    @Override
    public void invokeExecutorException(AttemptExecutor executor, AttemptContext context, Exception e) {
        AttemptStrategy strategy = executor.getStrategy();
        if (strategy instanceof ExecutorListener) {
            ((ExecutorListener) strategy).invokeExecutorException(executor, executor.getContext(), e);
        }
    }

    @Override
    public void invokeExecutorEnd(AttemptExecutor executor, AttemptContext context) {
        AttemptStrategy strategy = executor.getStrategy();
        if (strategy instanceof ExecutorListener) {
            ((ExecutorListener) strategy).invokeExecutorEnd(executor, executor.getContext());
        }
    }

    @Override
    public void invokeBeforeMethod(AttemptExecutor executor, AttemptContext context, TargetMethod method) {
        AttemptStrategy strategy = executor.getStrategy();
        if (strategy instanceof InvokeListener) {
            ((InvokeListener) strategy).invokeBeforeMethod(executor, executor.getContext(), method);
        }
    }


    @Override
    public void invokeAfterMethod(AttemptExecutor executor, AttemptContext context, TargetMethod method, AttemptResult record) {
        AttemptStrategy strategy = executor.getStrategy();
        if (strategy instanceof InvokeListener) {
            ((InvokeListener) strategy).invokeAfterMethod(executor, executor.getContext(), method, record);
        }
    }

    @Override
    public void invokeOriginException(AttemptExecutor executor, AttemptContext context, TargetMethod method, Throwable e) {
        AttemptStrategy strategy = executor.getStrategy();
        if (strategy instanceof InvokeListener) {
            ((InvokeListener) strategy).invokeOriginException(executor, executor.getContext(), method, e);
        }
    }
}
