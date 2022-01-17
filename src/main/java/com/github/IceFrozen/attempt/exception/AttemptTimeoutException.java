package com.github.IceFrozen.attempt.exception;

import com.github.IceFrozen.attempt.AttemptException;

public class AttemptTimeoutException extends AttemptException {
    public AttemptTimeoutException(String format, Object... arguments) {
        super(format, arguments);
    }
}
