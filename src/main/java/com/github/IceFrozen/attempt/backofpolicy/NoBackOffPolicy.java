package com.github.IceFrozen.attempt.backofpolicy;

/**
 * 空回退策略
 *
 * @author Jason Lee
 */
public class NoBackOffPolicy implements BackOffPolicy {
    @Override
    public void backOff() { }
}
