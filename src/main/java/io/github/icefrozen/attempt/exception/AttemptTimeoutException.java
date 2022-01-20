package io.github.icefrozen.attempt.exception;

import io.github.icefrozen.attempt.AttemptException;

public class AttemptTimeoutException extends AttemptException {
    public AttemptTimeoutException(String format, Object... arguments) {
        super(format, arguments);
    }
}
