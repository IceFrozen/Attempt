package io.github.icefrozen.attempt;

/**
 * the abstract context can store execution records
 *
 * @author Jason Lee
 */
public abstract class AbstractAttemptContext implements AttemptContext {
    protected String taskName;

    protected AttemptRecord record = new AttemptRecord();

    public AbstractAttemptContext(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public int getExecuteCount() {
        return record.getExecuteCount();
    }

    @Override
    public int getExceptionCount() {
        return record.getExceptionCount();
    }
}
