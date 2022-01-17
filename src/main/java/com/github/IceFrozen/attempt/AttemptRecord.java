package com.github.IceFrozen.attempt;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Attempt Statistics, which is used to collect statistics on the number of exceptions during Attempt execution
 *
 * @author Jaosn Lee
 */
public class AttemptRecord {

    protected AtomicInteger executeCount = new AtomicInteger(0);

    protected AtomicInteger exceptionCount = new AtomicInteger(0);

    public int incrementExecuteCount() {
        return executeCount.incrementAndGet();
    }

    public int incrementExceptionCount() {
        return exceptionCount.incrementAndGet();
    }

    public int getExecuteCount() {
        return executeCount.get();
    }

    public int getExceptionCount() {
        return exceptionCount.get();
    }

    public void reset() {
        executeCount.set(0);
        exceptionCount.set(0);
    }
}
