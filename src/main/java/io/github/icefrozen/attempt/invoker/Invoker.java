package io.github.icefrozen.attempt.invoker;

public interface Invoker<T> {

    T exec();

    default T exec(T defaultValue) {
        try {
            return exec();
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
