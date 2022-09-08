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

import java.util.List;

/**
 * Attempt context
 *
 * @author Jason Lee
 */
public interface AttemptContext {
    /**
     * get the last result
     *
     * @return AttemptResult
     */
    AttemptResult getLastResult();

    /**
     * record the result after the Attempt invoked
     *
     * @param record AttemptResult
     * @return record time
     */
    int record(AttemptResult record);

    /**
     * record the exception  after the Attempt invoked
     *
     * @param e exception
     * @return record time
     */
    int record(Class<? extends Throwable> e);

    /**
     * reset record count without clean AttemptResults
     */
    void reset();

    /**
     * reset record count and clean AttemptResults
     */
    void clean();

    /**
     * get last AttemptResult
     *
     * @return AttemptResult
     */
    AttemptResult getResult();

    /**
     * the count of execution
     *
     * @return execution count
     */
    int getExecuteCount();

    /**
     * The count of the specified exception occurred
     *
     * @param t specified exception
     * @return exception count
     */
    int getExceptionCount(Throwable t);

    /**
     * The count of the exception occurred
     *
     * @return exception count
     */
    default int getExceptionCount() {
        return getExceptionCount(null);
    }

    /**
     * get the all results
     *
     * @return AttemptResult
     */
    List<AttemptResult> getResults();
}
