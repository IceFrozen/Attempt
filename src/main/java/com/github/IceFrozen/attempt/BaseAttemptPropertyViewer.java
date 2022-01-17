package com.github.IceFrozen.attempt;

import com.github.IceFrozen.attempt.backofpolicy.BackOffPolicy;
import com.github.IceFrozen.attempt.backofpolicy.NoBackOffPolicy;
import com.github.IceFrozen.attempt.context.ResultContext;
import com.github.IceFrozen.attempt.listeners.AttemptListener;
import com.github.IceFrozen.attempt.strategy.AttemptStrategy;
import com.github.IceFrozen.attempt.strategy.RetryAttemptStrategy;
import com.github.IceFrozen.attempt.util.ExceptionClassifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Executor builder
 */
@SuppressWarnings("unchecked")
public abstract class BaseAttemptPropertyViewer<T extends BaseAttemptPropertyViewer<?>> {
    private int retryMax = 3;
    private int maxPollCount = -1;
    private String name;

    private final Map<Class<? extends Throwable>, Integer> exceptionRecord = new HashMap<>();
    private ExceptionClassifier exceptionClassifier = new ExceptionClassifier(true);

    private Class<? extends AttemptStrategy> strategy = RetryAttemptStrategy.class;
    private AttemptStrategy strategyInstance;

    private Class<? extends AbstractAttemptContext> context = ResultContext.class;
    private AbstractAttemptContext contextInstance;

    private Class<? extends BackOffPolicy> backOff = NoBackOffPolicy.class;
    private BackOffPolicy backOffInstance;

    private AttemptResult defaultValue;

    private final List<AttemptListener> listenerList = new ArrayList<>();

    private Function<AttemptContext, Boolean> endPointTry;

    private long timeout;

    public List<AttemptListener> getListeners() {
        return this.listenerList;
    }

    public AttemptResult defaultValue() {
        return defaultValue;
    }


    public T timeout(long timeout) {
        this.timeout = timeout;
        return (T) this;
    }

    public long timeout() {
        return this.timeout;
    }

    public T defaultValue(Object defaultValue) {
        this.defaultValue = new AttemptResult(defaultValue);
        return (T) this;
    }


    public T addListener(AttemptListener listener) {
        this.listenerList.add(listener);
        return (T) this;
    }

    public T endPointTry(Function<AttemptContext, Boolean> endPointTry) {
        this.endPointTry = endPointTry;
        return (T) this;
    }

    public Function<AttemptContext, Boolean> endPointTry() {
        return this.endPointTry;
    }

    public T registerExceptionRetryTime(Class<? extends Throwable> e, int count) {
        exceptionRecord.put(e, count);
        exceptionClassifier.replyOn(e);
        return (T) this;
    }

    public Map<Class<? extends Throwable>, Integer> exceptionRecord() {
        return this.exceptionRecord;
    }

    public int retryMax() {
        return retryMax;
    }


    public T retryMax(int retryMax) {
        this.retryMax = retryMax;
        return (T) this;
    }

    public int maxPollCount() {
        return maxPollCount;
    }


    public T maxPollCount(int maxPollCount) {
        this.maxPollCount = maxPollCount;
        return (T) this;
    }


    public T noThrow() {
        return noThrow(null);
    }


    public T noThrow(Object retValue) {
        this.defaultValue = new AttemptResult(retValue);
        return (T) this;
    }

    public String name() {
        return name;
    }


    public T name(String name) {
        this.name = name;
        return (T) this;
    }

    public ExceptionClassifier exceptionClassifier() {
        return exceptionClassifier;
    }

    public T exceptionClassifier(ExceptionClassifier exceptionClassifier) {
        this.exceptionClassifier = exceptionClassifier;
        return (T) this;
    }


    public Class<? extends AttemptStrategy> strategyClass() {
        return this.strategy;
    }


    public AttemptStrategy strategy() {
        return this.strategyInstance;
    }


    public T strategy(Class<? extends AttemptStrategy> strategy) {
        this.strategy = strategy;
        return (T) this;
    }

    public T strategy(AttemptStrategy strategyInstance) {
        this.strategyInstance = strategyInstance;
        return (T) this;
    }

    public Class<? extends AbstractAttemptContext> getContextClass() {
        return context;
    }

    public T context(Class<? extends AbstractAttemptContext> context) {
        this.context = context;
        return (T) this;
    }

    public AbstractAttemptContext context() {
        return contextInstance;
    }

    public T context(AbstractAttemptContext contextInstance) {
        this.contextInstance = contextInstance;
        return (T) this;
    }

    public Class<? extends BackOffPolicy> getBackOffClass() {
        return backOff;
    }

    public T backOff(Class<? extends BackOffPolicy> backOff) {
        this.backOff = backOff;
        return (T) this;
    }

    public BackOffPolicy backOff() {
        return backOffInstance;
    }


    public T backOff(BackOffPolicy backOffInstance) {
        this.backOffInstance = backOffInstance;
        return (T) this;
    }
}
