/*
 * MIT License
 *
 * Copyright (c) 2022 Jason Lee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.github.icefrozen.attempt;

import io.github.icefrozen.attempt.util.Convert;
import io.github.icefrozen.attempt.util.FixSizePriorityQueue;

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
