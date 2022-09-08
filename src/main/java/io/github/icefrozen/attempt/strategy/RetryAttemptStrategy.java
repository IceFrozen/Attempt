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

package io.github.icefrozen.attempt.strategy;


import io.github.icefrozen.attempt.exception.SneakyExceptionUtil;
import io.github.icefrozen.attempt.listeners.ExecutorListener;
import io.github.icefrozen.attempt.listeners.InvokeListener;
import io.github.icefrozen.attempt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 重试策略
 */
public class RetryAttemptStrategy extends AbstractAttemptStrategy implements ExecutorListener, InvokeListener {
    private static final Logger logger = LoggerFactory.getLogger(RetryAttemptStrategy.class);
    // 异常重试表
    public RetryAttemptStrategy(String strategyName, BaseAttemptPropertyViewer<BaseAttemptPropertyViewer<?>> properties) {
        super(strategyName, properties);
    }

    public RetryAttemptStrategy(String strategyName) {
        super(strategyName, new DefaultAttemptProperty());
    }

    @Override
    public boolean isEnd(AttemptContext context) {
        return context.getExecuteCount() >= properties.retryMax();
    }

    @Override
    public boolean back(
            AttemptResult record, AttemptContext context) {
        if (record.isSuccess()) {
            // 如果成功，则直接退出
            return true;
        }
        this.exceptionHandler(record, context);
        return super.back(record, context);
    }

    @SuppressWarnings("all")
    public void exceptionHandler(AttemptResult record, AttemptContext context) {
        if (record.isSuccess()) {
            return;
        }
        Throwable catchThrow = record.getCatchThrow();
        Class classify = properties.exceptionClassifier().classifyAndReturnClass(catchThrow);
        if (Objects.isNull(classify)) {
            SneakyExceptionUtil.sneakyThrow(record.getCatchThrow());
            return;
        }
        // 该异常是识别异常
        int count = context.record(classify);
        Integer classifyCount = properties.exceptionRecord().getOrDefault(classify, 0);
        if (count >= classifyCount) {
            logger.info("retry {} limit: ex:{}: limit:{}/{}", strategyName, classify, count, classifyCount);
            SneakyExceptionUtil.sneakyThrow(record.getCatchThrow());
        }
    }

    // 每次执行完毕，清空context 用于下次
    @Override
    public void invokeExecutorEnd(AttemptExecutor executor, AttemptContext context) {
        context.clean();
    }
}
