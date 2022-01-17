package com.github.IceFrozen.attempt;


import com.github.IceFrozen.attempt.exception.BaseException;

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
