package io.github.icefrozen.attempt.strategy;


import io.github.icefrozen.attempt.AttemptContext;
import io.github.icefrozen.attempt.AttemptResult;
import io.github.icefrozen.attempt.AttemptStatus;
import io.github.icefrozen.attempt.BaseAttemptPropertyViewer;

/**
 * 重试策略
 */
public interface AttemptStrategy {
    //1、是否结束
    boolean isEnd(AttemptContext context);
    // 一次调用声明周期结束后 true 进入循环，false 返回
    boolean back(AttemptResult record, AttemptContext context);
    // 策略名称
    String name();
    // 获取属性
    BaseAttemptPropertyViewer<? extends BaseAttemptPropertyViewer<?>> properties();
    // 获取状态
    AttemptStatus status();
    // 设置状态
    AttemptStatus status(AttemptStatus status);

}
