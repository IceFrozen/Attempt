package io.github.icefrozen.attempt.backofpolicy;


import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class SleepingBackOffPolicyTest {

    @Test
    public void fixedSleepingBackOffPolicBackOff() throws Exception {
        for (long i = 1000; i < 1009; i++) {
            FixedSleepingBackOffPolicy fixedSleepingBackOffPolicy = new FixedSleepingBackOffPolicy(i);
            Method getSleepAndIncrement = FixedSleepingBackOffPolicy.class.getDeclaredMethod("getNextInterval");
            getSleepAndIncrement.setAccessible(true);
            Long Longtime1 = (Long) getSleepAndIncrement.invoke(fixedSleepingBackOffPolicy);
            assertEquals(i, (long) Longtime1);
            Long Longtime2 = (Long) getSleepAndIncrement.invoke(fixedSleepingBackOffPolicy);
            assertEquals(i, (long) Longtime2);
            Long Longtime3 = (Long) getSleepAndIncrement.invoke(fixedSleepingBackOffPolicy);
            assertEquals(i, (long) Longtime3);
        }
    }

    @Test
    public void sleepingBackOffPolicyBackOff() throws Exception {
        SleepingBackOffPolicy sleepingBackOffPolicy = new SleepingBackOffPolicy();
        Method getSleepAndIncrement = SleepingBackOffPolicy.class.getDeclaredMethod("getSleepAndIncrement");
        getSleepAndIncrement.setAccessible(true);

        for (long i = 0; i < 10; i++) {
            Long Longtime1 = (Long) getSleepAndIncrement.invoke(sleepingBackOffPolicy);
        }

        SleepingBackOffPolicy sleepingBackOffPolicy2 = new SleepingBackOffPolicy(10, 1.5, 1000);
        for (long i = 0; i < 10; i++) {
            Long Longtime1 = (Long) getSleepAndIncrement.invoke(sleepingBackOffPolicy2);
        }
    }
}