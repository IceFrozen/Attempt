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
