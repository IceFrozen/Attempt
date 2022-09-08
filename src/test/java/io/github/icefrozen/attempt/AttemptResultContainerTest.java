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

import org.junit.Assert;
import org.junit.Test;
import java.util.List;

public class AttemptResultContainerTest {
    @Test
    public void testContainer() {
        AttemptResultContainer attemptResultContainer = new AttemptResultContainer(30);
        for (int i = 0; i < 100; i++) {
            AttemptResult attemptResult = new AttemptResult();
            attemptResult.setStartTime(i *1L);
            attemptResult.setEndTime(i + 1L);
            attemptResultContainer.record(attemptResult);
        }

        List<AttemptResult> lastRecord = attemptResultContainer.getLastRecord(10);

        for (int i = 0; i < lastRecord.size(); i++) {
            AttemptResult attemptResult = lastRecord.get(i);
            Assert.assertEquals((long) attemptResult.getEndTime(), 100 - i);
        }

        AttemptResult result = attemptResultContainer.getResult();
        Assert.assertEquals(100L, (long) result.getEndTime());
        Assert.assertSame(attemptResultContainer.getLastRecord(1).get(0), attemptResultContainer.getResult());

    }

    @Test
    public void testContainer2() {
        AttemptResultContainer attemptResultContainer = new AttemptResultContainer(1);
        for (int i = 0; i < 1; i++) {
            AttemptResult attemptResult = new AttemptResult();
            attemptResult.setStartTime(i *1L);
            attemptResult.setEndTime(i + 1L);
            attemptResultContainer.record(attemptResult);
        }

        List<AttemptResult> lastRecord = attemptResultContainer.getLastRecord(10);

        Assert.assertEquals(lastRecord.size(), (int) 1);

        AttemptResult result = attemptResultContainer.getResult();
        Assert.assertEquals(1, (long) result.getEndTime());
        Assert.assertSame(attemptResultContainer.getLastRecord(1).get(0), attemptResultContainer.getResult());

    }
}