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
