package io.github.icefrozen.attempt.exception;

public class ProxyException extends BaseException {
    public ProxyException(String message) {
        super(message);
    }

    public ProxyException(Throwable cause) {
        super(cause);
    }

    public ProxyException(String format, Object... arguments) {
        super(format, arguments);
    }
}
