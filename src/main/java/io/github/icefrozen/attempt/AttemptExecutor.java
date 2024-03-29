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

import io.github.icefrozen.attempt.exception.SneakyExceptionUtil;
import io.github.icefrozen.attempt.listeners.AttemptListener;
import io.github.icefrozen.attempt.listeners.ExecutorListener;
import io.github.icefrozen.attempt.listeners.InvokeListener;
import io.github.icefrozen.attempt.proxy.MethodInterceptor;
import io.github.icefrozen.attempt.proxy.TargetMethod;
import io.github.icefrozen.attempt.strategy.AbstractAttemptStrategy;
import io.github.icefrozen.attempt.strategy.AttemptStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AttemptExecutor implements MethodInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AttemptExecutor.class);

    private AttemptContext context;
    private AttemptStrategy strategy;
    private AttemptStatus status;
    private Long startTime;
    private Long endTime;

    private final List<InvokeListener> invokeListeners = new ArrayList<>();
    private final List<ExecutorListener> executorListener = new ArrayList<>();

    private AttemptResult defaultValue;


    public AttemptExecutor(AttemptContext context, AttemptStrategy strategy) {
        this.context = context;
        this.strategy = strategy;
        this.status = AttemptStatus.READY;
    }

    @Override
    public Object intercept(TargetMethod targetMethod) {
        Throwable lastError = null;
        start();
        try {
            while (status == AttemptStatus.STARTED && !strategy.isEnd(context)) {
                AttemptResult result = invoke(targetMethod);
                context.record(result);
                if (strategy.back(result, context)) {
                    break;
                }
            }
            assert context.getResult() != null;
        } catch (Exception e) {
            // 表示 Attempt 周期结束异常
            this.executorListener.forEach(listener -> listener.invokeExecutorException(this, this.getContext(), e));
            logger.info("retry attempt exception: {}", e.getMessage());
            lastError = e;
        }
        return handleResult(context.getResult(), lastError);
    }


    private Object handleResult(AttemptResult result, Throwable throwableException) {
        try {
            if (result != null && result.isSuccess()) {
                return result.getRetValue();
            }
            if (this.defaultValue != null) {
                return this.defaultValue.getRetValue();
            }

            Throwable lastThrowable = throwableException != null ? throwableException : result.getCatchThrow();
            SneakyExceptionUtil.sneakyThrow(lastThrowable);
            return null;
        } finally {
            end();
        }
    }

    public AttemptResult invoke(TargetMethod targetMethod) {
        AttemptResult record = new AttemptResult();
        // listeners
        this.invokeListeners.forEach(invokeListener -> invokeListener.invokeBeforeMethod(this, this.getContext(), targetMethod));
        try {
            record.setRetValue(targetMethod.invokeWithOriginalParams());
        } catch (Throwable e) {
            record.setCatchThrow(SneakyExceptionUtil.originExceptionUnWrapper(e));
            this.invokeListeners.forEach(listener -> listener.invokeOriginException(this, this.getContext(), targetMethod, e));
        } finally {
            record.setEndTime(System.currentTimeMillis());
            this.invokeListeners.forEach(invokeListener -> invokeListener.invokeAfterMethod(this, this.getContext(), targetMethod, record));
        }
        return record;
    }

    public void start() {
        this.status = AttemptStatus.STARTED;
        this.startTime = System.currentTimeMillis();
        this.executorListener.forEach(listener -> listener.invokeExecutorStart(this, this.getContext()));
    }

    public void end() {
        this.status = AttemptStatus.END;
        this.endTime = System.currentTimeMillis();
        this.executorListener.forEach(listener -> listener.invokeExecutorEnd(this, this.getContext()));
    }

    public void setListeners(List<AttemptListener> listeners) {
        for (AttemptListener listener : listeners) {
            addListener(listener);
        }
    }

    public List<AttemptListener> getListeners() {
        List<AttemptListener> listeners = new ArrayList<>();
        listeners.addAll(this.executorListener);
        listeners.addAll(this.invokeListeners);
        return Collections.unmodifiableList(listeners);
    }

    public void addListener(AttemptListener listener) {
        if (listener instanceof ExecutorListener) {
            this.executorListener.add((ExecutorListener) listener);
        }
        if (listener instanceof InvokeListener) {
            this.invokeListeners.add((InvokeListener) listener);
        }
    }

    public AttemptContext getContext() {
        return context;
    }

    public void setContext(AttemptContext context) {
        this.context = context;
    }

    public AttemptStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(AbstractAttemptStrategy strategy) {
        this.strategy = strategy;
    }

    public AttemptStatus getStatus() {
        return status;
    }

    public void setStatus(AttemptStatus status) {
        this.status = status;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public AttemptResult getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(AttemptResult defaultValue) {
        this.defaultValue = defaultValue;
    }

}
