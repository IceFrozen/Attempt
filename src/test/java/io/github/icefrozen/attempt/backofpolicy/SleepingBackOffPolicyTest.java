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