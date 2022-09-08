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
 * 根据基础时间逐步增长的休眠回退策略
 *
 * @author Jason Lee
 */
public class SleepingBackOffPolicy implements BackOffPolicy {

    public static final long DEFAULT_INITIAL_INTERVAL = 1000L;
    public static final long DEFAULT_MAX_INTERVAL = 20000L;
    public static final double DEFAULT_MULTIPLIER = 2;
    private volatile double multiplier = DEFAULT_MULTIPLIER;
    private volatile long interval = DEFAULT_INITIAL_INTERVAL;
    private volatile long maxInterval = DEFAULT_MAX_INTERVAL;

    public SleepingBackOffPolicy(long expSeed, double multiplier, long maxInterval) {
        this.interval = expSeed;
        this.multiplier = multiplier;
        this.maxInterval = maxInterval;
    }

    public SleepingBackOffPolicy() {

    }

    private synchronized long getSleepAndIncrement() {
        long sleep = this.interval;
        if (sleep > maxInterval) {
            sleep = maxInterval;
        }
        else {
            this.interval = getNextInterval();
        }
        return sleep;
    }

    /**
     * 获取下一次休眠时长
     */
    private long getNextInterval() {
        return (long) (this.interval * this.multiplier);
    }

    private void sleep() {
        long nextInterval = this.getSleepAndIncrement();
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
