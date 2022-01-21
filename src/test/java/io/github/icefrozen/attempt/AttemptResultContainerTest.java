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