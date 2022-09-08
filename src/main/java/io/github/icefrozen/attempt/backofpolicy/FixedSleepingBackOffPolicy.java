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
