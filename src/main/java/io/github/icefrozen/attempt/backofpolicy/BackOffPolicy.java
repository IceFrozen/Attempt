package io.github.icefrozen.attempt.backofpolicy;

/**
 * 回退策略
 *
 * @author Jason Lee
 */
public interface BackOffPolicy {
    void backOff();
}
