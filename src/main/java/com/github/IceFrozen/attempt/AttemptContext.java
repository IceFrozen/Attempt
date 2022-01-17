package com.github.IceFrozen.attempt;

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
