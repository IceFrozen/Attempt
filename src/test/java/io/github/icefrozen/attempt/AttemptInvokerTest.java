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

import io.github.icefrozen.attempt.exception.SneakyExceptionUtil;

import io.github.icefrozen.attempt.testBean.StaticMethodThrowExceptionBean;
import org.junit.Assert;
import org.junit.Test;

public class AttemptInvokerTest {
    public static Throwable origin;

    @Test
    public void testSneakyExceptionUtil() {
        try {
            throwException();
        } catch (Exception e) {
            Assert.assertSame(origin, e);
        }
    }

    public static void throwException() {
        try {
            Object o = StaticMethodThrowExceptionBean.throwException();
        } catch (Exception e) {
            origin = e;
            SneakyExceptionUtil.sneakyThrow(e);
        }
    }

    @Test
    public void testSneakyExceptionInvoke() {

        try {
            AttemptBuilder.retry(StaticMethodThrowExceptionBean::throwException).exec();
        } catch (Exception e) {
            Assert.assertSame("StaticMethodThrowExceptionBean's throwException", e.getMessage());
        }
        try {
            AttemptBuilder.polling(StaticMethodThrowExceptionBean::throwException).exec();
        } catch (Exception e) {
            Assert.assertSame("StaticMethodThrowExceptionBean's throwException", e.getMessage());
        }
    }
}
