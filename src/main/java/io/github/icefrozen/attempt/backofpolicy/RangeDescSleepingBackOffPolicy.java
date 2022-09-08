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

package io.github.icefrozen.attempt.backofpolicy;


import io.github.icefrozen.attempt.util.SecurityThreadWaitSleeper;

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
