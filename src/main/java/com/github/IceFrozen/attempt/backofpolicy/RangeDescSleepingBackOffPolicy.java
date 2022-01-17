package com.github.IceFrozen.attempt.backofpolicy;


import com.github.IceFrozen.attempt.util.SecurityThreadWaitSleeper;

/**
 * 范围递减等待时间策略
 *
 * @author Jason Lee
 */
public class RangeDescSleepingBackOffPolicy implements BackOffPolicy {
    public static final long DEFAULT_INITIAL_INTERVAL = 1000L;
    public static final long DEFAULT_MAX_INTERVAL = 20000L;
    public static final long DEFAULT_MIN_INTERVAL = 1000L;
    public static final double DEFAULT_MULTIPLIER = 2;
    private volatile double multiplier = DEFAULT_MULTIPLIER;
    private volatile long interval = DEFAULT_INITIAL_INTERVAL;
    private volatile long maxInterval = DEFAULT_MAX_INTERVAL;
    private volatile long minInterval = DEFAULT_MIN_INTERVAL;
    private volatile int backOffTimes = 0;

    public RangeDescSleepingBackOffPolicy(long expSeed, double multiplier, long maxInterval, long minInterval) {
        this.interval = expSeed;
        this.multiplier = multiplier;
        this.maxInterval = maxInterval;
        this.minInterval = minInterval;
    }

    public RangeDescSleepingBackOffPolicy(long expSeed, double multiplier) {
        this.interval = expSeed;
        this.multiplier = multiplier;
    }

    public RangeDescSleepingBackOffPolicy() {

    }

    public synchronized long getSleepAndIncrement() {

        long sleep = getNextInterval();
        if (sleep >= maxInterval) {
            sleep = maxInterval;
        }
        if (sleep < minInterval) {
            sleep = minInterval;
        }
        this.interval = sleep;
        return sleep;
    }

    /**
     * 获取下一次休眠时长
     */
    private long getNextInterval() {
        if (backOffTimes == 0) {
            return this.interval;
        }
        return (long) (this.interval * this.multiplier);
    }

    public void sleep() {
        long nextInterval = this.getSleepAndIncrement();
        this.sleep(nextInterval);

    }

    public void sleep(long duration) {
        if (duration == 0) {
            return;
        }
        SecurityThreadWaitSleeper.sleep(duration);
        this.backOffTimes++;
    }

    @Override
    public void backOff() {
        this.sleep();
    }
}
