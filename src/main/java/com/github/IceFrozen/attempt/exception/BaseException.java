package com.github.IceFrozen.attempt.exception;

/**
 * base exception
 *
 * @author Jason Lee
 */
public class BaseException extends FormativeException {
    public BaseException() {
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String format, Object... arguments) {
        super(format, arguments);
    }
}
