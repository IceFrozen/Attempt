package io.github.icefrozen.attempt.invoker;

import io.github.icefrozen.attempt.AbstractAttemptContext;
import io.github.icefrozen.attempt.AttemptBuilder;
import io.github.icefrozen.attempt.AttemptRecord;
import io.github.icefrozen.attempt.AttemptResult;
import io.github.icefrozen.attempt.testBean.ProxyDemo2;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.lang.reflect.Field;


public class InvokerTest {


    @Before
    public void before() {
        ProxyDemo2.field5 = 0;
    }
    @After
    public void after() {
        ProxyDemo2.field5 = 0;
    }

    @Test
    public void testSimpleSuccessCall() throws Exception {
        ProxyDemo2 proxyDemo2 = new ProxyDemo2();
        AttemptBuilder.Retry<ProxyDemo2> retryBuilder = new AttemptBuilder.Retry<>(proxyDemo2);
        ProxyDemo2 retryProxy = retryBuilder.build();

        RetryAttemptInvoker<Integer> retry = AttemptBuilder.retry(retryProxy::getField1);
        int field1 = (int) retry.exec();

        Assert.assertEquals(proxyDemo2.count, 1);
        Assert.assertEquals(proxyDemo2.field1, field1);

        AbstractAttemptContext context = retryBuilder.context();
        Field record = AbstractAttemptContext.class.getDeclaredField("record");
        record.setAccessible(true);
        AttemptRecord o = (AttemptRecord) record.get(context);
        Assert.assertEquals(0, o.getExecuteCount());
        Assert.assertEquals(0, o.getExceptionCount());
    }

    @Test
    public void testSimpleSuccessCall2() throws Exception {
        ProxyDemo2 proxyDemo2 = new ProxyDemo2();
        AttemptBuilder.Retry<ProxyDemo2> retryBuilder = new AttemptBuilder.Retry<>(proxyDemo2);
        ProxyDemo2 retryProxy = retryBuilder.build();
        RetryAttemptInvoker<Integer> retry = AttemptBuilder.retry(retryProxy::getField1);

        int retValue = (int) retry.getResult().getRetValue();

        Assert.assertEquals(proxyDemo2.count, 1);
        Assert.assertEquals(proxyDemo2.field1, retValue);

        AbstractAttemptContext context = retryBuilder.context();
        Field record = AbstractAttemptContext.class.getDeclaredField("record");
        record.setAccessible(true);
        AttemptRecord o = (AttemptRecord) record.get(context);
        Assert.assertEquals(0, o.getExecuteCount());
        Assert.assertEquals(0, o.getExceptionCount());
    }


    @Test
    public void testSimpleErrorCall() throws Exception {
        // 创建需要重试的对象
        ProxyDemo2 proxyDemo2 = new ProxyDemo2();
        RetryAttemptInvoker<Integer> retry = AttemptBuilder.retry(proxyDemo2::errorMethod);
        proxyDemo2.count = 0;
        int exec = retry.exec(10);

        AttemptResult result = retry.getResult();
        Assert.assertFalse(result.isSuccess());

        Assert.assertFalse(result.getRetValue() instanceof ArithmeticException);
        Assert.assertEquals(10, exec);
    }


    @Test
    public void testRetryExec() {
        int executeCount = 0;
        ProxyDemo2.field5 = 0;
        try {
            // 第一种调用方式
            //Integer exec2 = Retry.retry(ProxyDemo2::getFileStatic).exec();
            // 第二种方法
            AttemptBuilder.retry(() -> ProxyDemo2.staticErrorMethod()).exec();
            // 第三种方法 设置属性

        } catch (Exception e) {
            Assert.assertTrue(e instanceof ArithmeticException);
            Assert.assertEquals(3, ProxyDemo2.field5);
        }
        // 带默认值的调用方式
        ProxyDemo2.field5 = 0;

        Integer defaultValue = 10;
        Integer exec2 = AttemptBuilder.retry(ProxyDemo2::staticErrorMethod)
                .exec(defaultValue);
        Assert.assertEquals(3, ProxyDemo2.field5);
        Assert.assertEquals(exec2, defaultValue);
        // 带默认值的调用方式
        ProxyDemo2.field5 = 0;
    }


    @Test
    public void testRetry2Exec() {
        int executeCount = 0;
        ProxyDemo2.field5 = 0;
        try {
            // 第一种调用方式
            //Integer exec2 = Retry.retry(ProxyDemo2::getFileStatic).exec();
            // 第二种方法
            AttemptResult result = AttemptBuilder.retry(ProxyDemo2::getVoidError).getResult();
            Integer exec2  = (Integer) result.getRetValue();
            // 第三种方法 设置属性

        } catch (Exception e) {
            Assert.assertTrue(e instanceof ArithmeticException);
            Assert.assertEquals(3, ProxyDemo2.field5);
        }
        // 带默认值的调用方式
        ProxyDemo2.field5 = 0;

        Integer defaultValue = 10;
        Integer exec2 = AttemptBuilder.retry(ProxyDemo2::staticErrorMethod).exec(defaultValue);
        Assert.assertEquals(3, ProxyDemo2.field5);
        Assert.assertEquals(exec2, defaultValue);
        // 带默认值的调用方式
        ProxyDemo2.field5 = 0;
    }

    @Test
    public void testRetry3Exec() {
        ProxyDemo2.field5 = 0;
        AttemptBuilder.retry(ProxyDemo2::getVoid).exec();
        Assert.assertEquals(1, ProxyDemo2.field5);
        ProxyDemo2.field5 = 0;
    }
}