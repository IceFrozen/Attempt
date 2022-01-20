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
