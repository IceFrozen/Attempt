package io.github.icefrozen.attempt.exception;


import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * 支持字符串格式化的异常
 * 不支持线程
 * @author Jason Lee
 */
public class FormativeException extends RuntimeException {
    private int[] indices;
    private int usedCount;
    protected String message;
    private transient Throwable throwable;
    private transient Object[]  args;

    public FormativeException() {
        super();
    }

    public FormativeException(String message) {
        this.message = message;
    }

    public FormativeException(Throwable cause) {
        this.throwable = cause;
        this.message = ExceptionUtils.getStackTrace(throwable);
    }

    public FormativeException(String format, Object... arguments) {
        init(format, arguments);
        fillInStackTrace();
        formatMessage();
        if (throwable != null) {
            this.message += System.lineSeparator() + ExceptionUtils.getStackTrace(throwable);
        }
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        String s = getClass().getName();
        String message = getMessage();
        return (message != null) ? (s + ": " + message) : s;
    }

    private void init(String format, Object... arguments) {
        this.message = format;
        this.args = arguments;
        final int len = Math.max(1, format == null ? 0 : format.length() >> 1); // divide by 2
        this.indices = new int[len]; // LOG4J2-1542 ensure non-zero array length
        final int placeholders = ParameterFormatter.countArgumentPlaceholders2(format, indices);
        initThrowable(arguments, placeholders);
        this.usedCount = Math.min(placeholders, arguments == null ? 0 : arguments.length);
    }

    private void initThrowable(final Object[] params, final int usedParams) {
        if (params != null) {
            final int argCount = params.length;
            if (usedParams < argCount && this.throwable == null && params[argCount - 1] instanceof Throwable) {
                this.throwable = (Throwable) params[argCount - 1];
            }
        }
    }

    private String formatMessage() {
        this.message = formatMessage(this.message, this.usedCount, this.indices, this.args);
        return this.message;
    }

    private static String formatMessage(String format, int usedCount, int[] indices ,Object... arguments) {
        if(arguments == null || arguments.length == 0) {
            return format;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (indices[0] < 0) {
            ParameterFormatter.formatMessage(stringBuilder, format, arguments, usedCount);
        } else {
            ParameterFormatter.formatMessage2(stringBuilder, format, arguments, usedCount, indices);
        }

        return stringBuilder.toString();
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

}
