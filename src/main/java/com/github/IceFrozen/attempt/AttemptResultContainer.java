package com.github.IceFrozen.attempt;

import com.github.IceFrozen.attempt.util.Convert;
import com.github.IceFrozen.attempt.util.FixSizePriorityQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * AttemptResultContainer is used to store the results of an Attempt execution.
 *
 * @author Jason Lee
 */
public class AttemptResultContainer extends AttemptRecord {

    FixSizePriorityQueue<AttemptResult> queen;

    Comparator<AttemptResult> comparator = (r1, r2) -> {
        Integer integer = Convert.toInt(r2.getEndTime() - r1.getEndTime());
        if (integer == 0) {
            return -1;
        }
        return integer;
    };

    public AttemptResultContainer(int maxSize) {
        maxSize = Math.max(1, maxSize);
        this.queen = new FixSizePriorityQueue<>(maxSize, comparator);
    }

    public void record(AttemptResult result) {
        synchronized (this) {
            this.queen.add(result);
        }
        if (result.isSuccess()) {
            super.incrementExecuteCount();
        } else {
            this.incrementExceptionCount();
        }
    }

    public AttemptResult getResult() {
        return this.queen.peekFirst();
    }

    public AttemptResult getLastRecord() {
        List<AttemptResult> lastRecord = getLastRecord(2);
        if (lastRecord.size() != 2) {
            return null;
        }
        return lastRecord.get(1);
    }

    public List<AttemptResult> getLastRecord(int count) {
        count = Math.max(count, this.queen.size());

        ArrayList<AttemptResult> attemptResults = new ArrayList<>(count);
        AttemptResult attemptResult = this.queen.pollFirst();
        while (count > 0 && attemptResult != null) {
            attemptResults.add(attemptResult);
            attemptResult = this.queen.pollFirst();
        }
        this.queen.addAll(attemptResults);

        return Collections.unmodifiableList(attemptResults);
    }

    @Override
    public void reset() {
        super.reset();
    }

    public void clean() {
        this.reset();
        this.queen.clear();
    }
}
