package com.github.IceFrozen.attempt;

/**
 * Attempt status
 *
 * @author Jason Lee
 */
public enum AttemptStatus {
    CREATED,
    READY,
    STARTED,
    BLOCKED,
    END,

    PROGRESS,
    HOLDING,
    EXCEPTION;
}
