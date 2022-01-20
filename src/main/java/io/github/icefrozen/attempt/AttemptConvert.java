package io.github.icefrozen.attempt;

public interface AttemptConvert<I, O> {
    O convert(I i);
}
