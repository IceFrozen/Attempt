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

package io.github.icefrozen.attempt;

import io.github.icefrozen.attempt.util.Convert;

/**
 * 请求记录
 */
public class AttemptResult {
    // 调用开始时间
    private Long startTime;
    private Object retValue;
    private Throwable exception;
    // 调用结束时间
    private Long endTime;

    public AttemptResult() {
        this(null, null);
    }

    public AttemptResult(Object retValue) {
        this(retValue, null);
    }

    public AttemptResult(Object retValue, Throwable exception) {
        this.startTime = System.currentTimeMillis();
        this.retValue = retValue;
        this.exception = exception;
    }

    public boolean isSuccess() {
        return this.startTime != null && this.endTime != null && exception == null;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Object getRetValue() {
        return retValue;
    }

    public <T> T getRetValue(Class<T> type) {
        return Convert.valueToType(this.retValue, type);
    }

    public void setRetValue(Object retValue) {
        this.retValue = retValue;
    }

    public Throwable getCatchThrow() {
        return exception;
    }

    public void setCatchThrow(Throwable exception) {
        this.exception = exception;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "AttemptResult{" +
                "startTime=" + startTime +
                ", retValue=" + retValue +
                ", exception=" + exception +
                ", endTime=" + endTime +
                '}';
    }
}
