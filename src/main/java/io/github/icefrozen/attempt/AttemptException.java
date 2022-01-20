package io.github.icefrozen.attempt;


import io.github.icefrozen.attempt.exception.BaseException;

public class AttemptException extends BaseException {

    public AttemptException(Throwable e, String format, Object... arguments) {
        super(format, arguments);
        addSuppressed(e);
    }

    public AttemptException(String format, Object... arguments) {
        super(format, arguments);
    }

    public AttemptException(Throwable e) {
        this(e, e.getMessage());
    }

    public AttemptException(String message) {
        super(message);
    }

}
