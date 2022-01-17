package com.github.IceFrozen.attempt.backofpolicy;

import com.github.IceFrozen.attempt.util.SecurityThreadWaitSleeper;

/**
 * 固定的休眠策略
 *
 * @author Jason Lee
 */
public class FixedSleepingBackOffPolicy implements BackOffPolicy {
    private long sleepTime = 1000L;

    public FixedSleepingBackOffPolicy() {
        this(1000L);
    }

    public FixedSleepingBackOffPolicy(Long time) {
        this.sleepTime = time;
    }

    /**
     * 获取下一次休眠时长
     */
    private synchronized long getNextInterval() {
        return this.sleepTime;
    }

    private void sleep() {
        long nextInterval = this.getNextInterval();
        this.sleep(nextInterval);
    }

    private void sleep(long duration) {
        if (duration == 0) {
            return;
        }
        SecurityThreadWaitSleeper.sleep(duration);
    }

    @Override
    public void backOff() {
        this.sleep();
    }
}
