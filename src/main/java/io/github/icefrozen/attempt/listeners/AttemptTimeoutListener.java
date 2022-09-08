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
import io.github.icefrozen.attempt.exception.AttemptTimeoutException;
import io.github.icefrozen.attempt.proxy.TargetMethod;
import io.github.icefrozen.attempt.strategy.AttemptStrategy;

public class AttemptTimeoutListener implements ExecutorListener, InvokeListener {
    private Long startTime;
    private Long timeout;
    private Long endTime;
    private AttemptStrategy strategy;

    @Override
    public void invokeExecutorStart(
            AttemptExecutor executor, AttemptContext context) {
        startTime = executor.getStartTime();
        timeout = executor.getStrategy().properties().timeout();
        endTime = startTime + timeout;
        strategy = executor.getStrategy();
    }

    @Override
    public void invokeBeforeMethod(
            AttemptExecutor executor, AttemptContext context, TargetMethod method) {
        Long timePoint = System.currentTimeMillis();
        if (isTimeout(timePoint)) {
            throw new AttemptTimeoutException("the attempt {} is timeout: last time:{} ms:", strategy.name(), timePoint - startTime);
        }
    }

    @Override
    public void invokeAfterMethod(
            AttemptExecutor executor, AttemptContext context, TargetMethod method, AttemptResult record) {
        Long timePoint = record.getEndTime();
        if (isTimeout(timePoint)) {
            throw new AttemptTimeoutException("the attempt {} is timeout: last time:{} ms:", strategy.name(), timePoint - startTime);
        }
    }

    @Override
    public void invokeOriginException(
            AttemptExecutor executor, AttemptContext context, TargetMethod method, Throwable e) {

    }

    @Override
    public void invokeExecutorException(AttemptExecutor executor, AttemptContext context, Exception e) {

    }

    @Override
    public void invokeExecutorEnd(
            AttemptExecutor executor, AttemptContext context) {
        endTime = 0L;
    }


    public boolean isTimeout(Long timePoint) {
        return timePoint > endTime;
    }
}
