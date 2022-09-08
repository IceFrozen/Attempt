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
