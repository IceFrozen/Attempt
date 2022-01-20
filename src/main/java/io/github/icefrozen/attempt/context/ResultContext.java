package io.github.icefrozen.attempt.context;

import io.github.icefrozen.attempt.AbstractAttemptContext;
import io.github.icefrozen.attempt.AttemptResult;
import io.github.icefrozen.attempt.AttemptResultContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ResultContext the implementation of  the AbstractAttemptContext
 *
 * @author Jason Lee
 */
public class ResultContext extends AbstractAttemptContext {
    private static final Logger logger = LoggerFactory.getLogger(ResultContext.class);
    private final AttemptResultContainer container;
    private final Map<Class<? extends Throwable>, AtomicInteger> exceptionRecord = new ConcurrentHashMap<>();

    public ResultContext() {
        this(1);
    }

    public ResultContext(int resultCount) {
        super("ResultContext[" + resultCount + "]");
        container = new AttemptResultContainer(resultCount);
    }

    @Override
    public AttemptResult getLastResult() {
        return this.container.getResult();
    }

    @Override
    public int record(AttemptResult result) {
        int executeCount = record.incrementExecuteCount();
        this.container.record(result);
        return executeCount;
    }

    @Override
    public int record(Class<? extends Throwable> e) {
        AtomicInteger exceptionCountInMap = this.exceptionRecord.getOrDefault(e, new AtomicInteger(0));
        this.exceptionRecord.put(e, exceptionCountInMap);
        exceptionCountInMap.incrementAndGet();
        record.incrementExceptionCount();
        return exceptionCountInMap.get();
    }

    @Override
    public void reset() {
        this.record.reset();
        this.exceptionRecord.clear();
    }

    @Override
    public void clean() {
        reset();
        this.container.clean();
    }

    @Override
    public AttemptResult getResult() {
        return this.getLastResult();
    }

    @Override
    public int getExceptionCount(Throwable t) {
        if (t == null) {
            return super.getExceptionCount();
        }
        Class<? extends Throwable> aClass = t.getClass();
        return this.exceptionRecord.getOrDefault(aClass, new AtomicInteger(0)).get();
    }

    @Override
    public List<AttemptResult> getResults() {
        return Collections.singletonList(this.getResult());
    }
}
