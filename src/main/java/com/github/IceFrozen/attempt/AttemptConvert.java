package com.github.IceFrozen.attempt;

public interface AttemptConvert<I, O> {
    O convert(I i);
}
