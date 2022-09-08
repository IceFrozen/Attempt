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
import io.github.icefrozen.attempt.backofpolicy.FixedSleepingBackOffPolicy;
import io.github.icefrozen.attempt.backofpolicy.NoBackOffPolicy;
import io.github.icefrozen.attempt.invoker.RetryAttemptInvoker;
import io.github.icefrozen.attempt.invoker.ThrowSafetyFunctionInvoker;
import io.github.icefrozen.attempt.listeners.ExecutorListener;
import io.github.icefrozen.attempt.listeners.InvokeListener;
import io.github.icefrozen.attempt.proxy.TargetMethod;
import io.github.icefrozen.attempt.strategy.RetryAttemptStrategy;
import io.github.icefrozen.attempt.testBean.ProxyDemo2;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

public class AttemptRetryTest {

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
        int field1 = retryProxy.getField1();

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
    public void testSimpleErrorCall() throws Exception {
        // 创建需要重试的对象
        ProxyDemo2 proxyDemo2 = new ProxyDemo2();
        // 构建重试代理
        AttemptBuilder.Retry<ProxyDemo2> retryBuilder = new AttemptBuilder.Retry<>(proxyDemo2);
        ProxyDemo2 retryProxy = retryBuilder.build();

        proxyDemo2.count = 0;
        try {
            retryProxy.errorMethod();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ArithmeticException);
            // 原始对象中执行了3次
            Assert.assertEquals(proxyDemo2.count, retryBuilder.retryMax());
            AbstractAttemptContext context = retryBuilder.context();
            Field record = AbstractAttemptContext.class.getDeclaredField("record");
            record.setAccessible(true);
            AttemptRecord o = (AttemptRecord) record.get(context);
            Assert.assertEquals(0, o.getExecuteCount());
            Assert.assertEquals(0, o.getExceptionCount());
        }
    }

    @Test
    public void testSimpleSuccessLoopCall() throws Exception {
        // 创建需要重试的对象
        ProxyDemo2 proxyDemo2 = new ProxyDemo2();
        // 构建重试代理
        AttemptBuilder.Retry<ProxyDemo2> retryBuilder = new AttemptBuilder.Retry<>(proxyDemo2);

        ProxyDemo2 retryProxy = retryBuilder.build();

        AbstractAttemptContext context = retryBuilder.context();
        Field record = AbstractAttemptContext.class.getDeclaredField("record");
        record.setAccessible(true);
        AttemptRecord o = (AttemptRecord) record.get(context);
        // 验证连续调用
        for (int i = 0; i < 12; i++) {
            Assert.assertEquals(0, o.getExecuteCount());
            Assert.assertEquals(0, o.getExceptionCount());
            int field1 = retryProxy.getField1();
            Assert.assertEquals(proxyDemo2.count, i + 1);
            Assert.assertEquals(proxyDemo2.field1, field1);
            Assert.assertEquals(0, o.getExecuteCount());
            Assert.assertEquals(0, o.getExceptionCount());
        }
        Assert.assertEquals(proxyDemo2.count, 12);
        Assert.assertEquals(0, o.getExecuteCount());
        Assert.assertEquals(0, o.getExceptionCount());
    }

    /**
     * 简单构建
     */
    @Test
    public void testSimpleErrorLoopCall() throws Exception {
        // 创建需要重试的对象
        ProxyDemo2 proxyDemo2 = new ProxyDemo2();
        AttemptBuilder.Retry<ProxyDemo2> retryBuilder = new AttemptBuilder.Retry<>(proxyDemo2);
        ProxyDemo2 retryProxy = retryBuilder.build();
        for (int i = 0; i < 12; i++) {
            proxyDemo2.count = 0;
            try {
                retryProxy.errorMethod();
            } catch (Exception e) {
                Assert.assertTrue(e instanceof ArithmeticException);
                // 原始对象中执行了3次
                Assert.assertEquals(proxyDemo2.count, retryBuilder.retryMax());
                Assert.assertTrue(e instanceof ArithmeticException);
                // 原始对象中执行了3次
                Assert.assertEquals(proxyDemo2.count, retryBuilder.retryMax());
                AbstractAttemptContext context = retryBuilder.context();
                Field record = AbstractAttemptContext.class.getDeclaredField("record");
                record.setAccessible(true);
                AttemptRecord o = (AttemptRecord) record.get(context);
                Assert.assertEquals(0, o.getExecuteCount());
                Assert.assertEquals(0, o.getExceptionCount());
            }
        }
    }

    @Test
    public void testTimeoutSuccessCall1() throws Exception {
        // 创建需要重试的对象
        ProxyDemo2.field5 = 0;
        ProxyDemo2 proxyDemo2 = new ProxyDemo2();
        // 构建重试代理
        AttemptBuilder.Retry<ProxyDemo2> retryBuilder = new AttemptBuilder.Retry<>(proxyDemo2);
        FixedSleepingBackOffPolicy bp = new FixedSleepingBackOffPolicy(400L); // 每次休眠4秒
        ProxyDemo2 retryProxy = retryBuilder.timeout(300)
                .retryMax(3).backOff(bp)
                .build();

        // 验证连续调用
        proxyDemo2.count = 0;
        int fileStatic = retryProxy.getFileStatic();
        Assert.assertEquals(ProxyDemo2.field5, fileStatic);
        AbstractAttemptContext context = retryBuilder.context();
        Field record = AbstractAttemptContext.class.getDeclaredField("record");
        record.setAccessible(true);
        AttemptRecord o = (AttemptRecord) record.get(context);
        Assert.assertEquals(0, o.getExecuteCount());
        Assert.assertEquals(0, o.getExceptionCount());
        ProxyDemo2.field5 = 0;
    }


    @Test
    public void testTimeouErrorCall2() throws Exception {
        // 创建需要重试的对象
        ProxyDemo2.field5 = 0;
        ProxyDemo2 proxyDemo2 = new ProxyDemo2();
        // 构建重试代理
        AttemptBuilder.Retry<ProxyDemo2> retryBuilder = new AttemptBuilder.Retry<>(proxyDemo2);
        FixedSleepingBackOffPolicy bp = new FixedSleepingBackOffPolicy(100L); // 每次休眠4秒
        ProxyDemo2 retryProxy = retryBuilder.timeout(300)
                .retryMax(100).backOff(bp)
                .build();
        proxyDemo2.count = 0;
        try {
            int fileStatic = retryProxy.errorMethod();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof AttemptException);
            Assert.assertTrue(StringUtils.contains(e.getMessage(), "timeout"));
            // 原始对象中执行了3次
            Assert.assertEquals(proxyDemo2.count, 3);
            AbstractAttemptContext context = retryBuilder.context();
            Field record = AbstractAttemptContext.class.getDeclaredField("record");
            record.setAccessible(true);
            AttemptRecord o = (AttemptRecord) record.get(context);
            Assert.assertEquals(0, o.getExecuteCount());
            Assert.assertEquals(0, o.getExceptionCount());
        }
        ProxyDemo2.field5 = 0;
    }

    @Test
    public void testTimeouErrorCall3() throws Exception {
        // 创建需要重试的对象
        ProxyDemo2.field5 = 0;
        ProxyDemo2 proxyDemo2 = new ProxyDemo2();
        // 构建重试代理
        AttemptBuilder.Retry<ProxyDemo2> retryBuilder = new AttemptBuilder.Retry<>(proxyDemo2);
        FixedSleepingBackOffPolicy bp = new FixedSleepingBackOffPolicy(100L);
        ProxyDemo2 retryProxy = retryBuilder.timeout(300)
                .retryMax(100).backOff(bp)
                .registerExceptionRetryTime(AttemptException.class, 10)
                .build();
        proxyDemo2.count = 0;
        try {
            int fileStatic = retryProxy.errorMethod();
        } catch (ArithmeticException e) {

            Assert.assertEquals(proxyDemo2.count, 1);
            AbstractAttemptContext context = retryBuilder.context();
            Field record = AbstractAttemptContext.class.getDeclaredField("record");
            record.setAccessible(true);
            AttemptRecord o = (AttemptRecord) record.get(context);
            Assert.assertEquals(0, o.getExecuteCount());
            Assert.assertEquals(0, o.getExceptionCount());
        }
        ProxyDemo2.field5 = 0;
    }

    @Test
    public void testTimeoutErrorCall4() throws Exception {
        // 创建需要重试的对象
        ProxyDemo2.field5 = 0;
        ProxyDemo2 proxyDemo2 = new ProxyDemo2();
        // 构建重试代理
        AttemptBuilder.Retry<ProxyDemo2> retryBuilder = new AttemptBuilder.Retry<>(proxyDemo2);
        FixedSleepingBackOffPolicy bp = new FixedSleepingBackOffPolicy(100L);
        ProxyDemo2 retryProxy = retryBuilder.timeout(300)
                .retryMax(100).backOff(bp)
                .registerExceptionRetryTime(ArithmeticException.class, 10)
                .build();

        // 验证连续调用
        proxyDemo2.count = 0;
        try {
            int fileStatic = retryProxy.errorMethod();
        } catch (AttemptException e) {
            Assert.assertTrue(StringUtils.contains(e.getMessage(), "timeout"));
            Assert.assertEquals(proxyDemo2.count, 3);
            AbstractAttemptContext context = retryBuilder.context();
            Field record = AbstractAttemptContext.class.getDeclaredField("record");
            record.setAccessible(true);
            AttemptRecord o = (AttemptRecord) record.get(context);
            Assert.assertEquals(0, o.getExecuteCount());
            Assert.assertEquals(0, o.getExceptionCount());
        }
        ProxyDemo2.field5 = 0;
    }

    @Test
    public void testTimeoutErrorCall() throws Exception {
        // 创建需要重试的对象
        ProxyDemo2 proxyDemo2 = new ProxyDemo2();
        // 构建重试代理
        AttemptBuilder.Retry<ProxyDemo2> retryBuilder = new AttemptBuilder.Retry<>(proxyDemo2);
        FixedSleepingBackOffPolicy bp = new FixedSleepingBackOffPolicy(400L); // 每次休眠4秒
        ProxyDemo2 retryProxy = retryBuilder.timeout(300)
                .retryMax(3).backOff(bp)
                .build();

        // 验证连续调用
        proxyDemo2.count = 0;
        try {
            retryProxy.errorMethod();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof AttemptException);
            Assert.assertTrue(StringUtils.contains(e.getMessage(), "timeout"));
            // 原始对象中执行了3次
            Assert.assertEquals(proxyDemo2.count, 1);

            RetryAttemptStrategy strategy = (RetryAttemptStrategy) retryBuilder.strategy();
            AbstractAttemptContext context = retryBuilder.context();
            Field record = AbstractAttemptContext.class.getDeclaredField("record");
            record.setAccessible(true);
            AttemptRecord o = (AttemptRecord) record.get(context);
            Assert.assertEquals(0, o.getExecuteCount());
            Assert.assertEquals(0, o.getExceptionCount());
        }
    }


    @Test
    public void attempListenerTest1() {
        ProxyDemo2 proxyDemo2 = new ProxyDemo2();
        AttemptBuilder.Retry<ProxyDemo2> retryBuilder = new AttemptBuilder.Retry<>(proxyDemo2);
        CountAttemptListener attempListener = new CountAttemptListener();
        ProxyDemo2 retryProxy = retryBuilder
                .retryMax(4)
                .registerExceptionRetryTime(Exception.class, 2)
                .backOff(NoBackOffPolicy.class)
                .addListener(attempListener).build();

        Attempt<ProxyDemo2> attempt = retryBuilder.attempt();
        ProxyDemo2 originObject = attempt.getOriginObject();
        try {
            int field1 = retryProxy.errorMethod();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ArithmeticException);
            // 原始对象执行方法为3次
            Assert.assertEquals(2, originObject.count);
            Assert.assertEquals(1, attempListener.invokeExecutorStartCount);
            Assert.assertEquals(1, attempListener.invokeExecutorEndCount);
            Assert.assertEquals(2, attempListener.invokeAfterMethodCount);
            Assert.assertEquals(2, attempListener.invokeOriginExceptionCount);
        }
    }

    @Test
    public void attempListenerTest2() {
        ProxyDemo2 proxyDemo2 = new ProxyDemo2();
        AttemptBuilder.Retry<ProxyDemo2> retryBuilder = new AttemptBuilder.Retry<>(proxyDemo2);
        CountAttemptListener attemptListener = new CountAttemptListener();
        ProxyDemo2 retryProxy = retryBuilder
                .retryMax(4)
                .backOff(NoBackOffPolicy.class)
                .addListener(attemptListener).build();
        Attempt<ProxyDemo2> attempt = retryBuilder.attempt();
        ProxyDemo2 originObject = attempt.getOriginObject();
        try {
            int field1 = retryProxy.errorMethod();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ArithmeticException);
            Assert.assertEquals(4, originObject.count);
            Assert.assertEquals(1, attemptListener.invokeExecutorStartCount);
            Assert.assertEquals(1, attemptListener.invokeExecutorEndCount);
            Assert.assertEquals(4, attemptListener.invokeAfterMethodCount);
            Assert.assertEquals(4, attemptListener.invokeOriginExceptionCount);
        }
    }

    @Test
    public void testRetry2() {
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
        Integer exec2 = AttemptBuilder.retry(ProxyDemo2::staticErrorMethod).exec(defaultValue);
        Assert.assertEquals(3, ProxyDemo2.field5);
        Assert.assertEquals(exec2, defaultValue);
        // 带默认值的调用方式
        ProxyDemo2.field5 = 0;
    }

    /**
     * 获取执行器调用方式
     */
    @Test
    public void testRetry3() {
        int executeCount = 0;
        ProxyDemo2.field5 = 0;
        try {
            RetryAttemptInvoker<Integer> attemptRetryInvoker = AttemptBuilder.retry(() -> {
                int fileStatic = ProxyDemo2.staticErrorMethod();
                return fileStatic;
            });
            // 获得 attempt
            Attempt<ThrowSafetyFunctionInvoker<Integer>> attempt = attemptRetryInvoker.getAttempt();
            attemptRetryInvoker.exec();

        } catch (Exception e) {
            Assert.assertTrue(e instanceof ArithmeticException);
            Assert.assertEquals(3, ProxyDemo2.field5);
        }
    }

    @Test
    public void testRetry4() {
        int executeCount = 0;
        ProxyDemo2.field5 = 0;
        try {
            // 第一种调用方式
            //Integer exec2 = Retry.retry(ProxyDemo2::getFileStatic).exec();
            // 第二种方法
            AttemptBuilder.retry(() -> ProxyDemo2.staticErrorMethod()).retryMax(10).exec();
            // 第三种方法 设置属性
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ArithmeticException);
            Assert.assertEquals(10, ProxyDemo2.field5);
        }

        // 带默认值的调用方式
        ProxyDemo2.field5 = 0;
        Integer defaultValue = 10;
        Integer exec2 = AttemptBuilder.retry(ProxyDemo2::staticErrorMethod).exec(defaultValue);
        Assert.assertEquals(3, ProxyDemo2.field5);
        Assert.assertEquals(exec2, defaultValue);
    }

    @Test
    public void testRetry4_2() {
        ProxyDemo2.field5 = 0;
        CountAttemptListener attempListener = new CountAttemptListener();
        try {

            Integer exec = AttemptBuilder.retry(ProxyDemo2::staticErrorMethod)
                    .retryMax(10)
                    .registerExceptionRetryTime(ArithmeticException.class, 3)
                    .addListener(attempListener)
                    .exec();


        } catch (Exception e) {
            Assert.assertTrue(e instanceof ArithmeticException);
            Assert.assertEquals(3, ProxyDemo2.field5);
            Assert.assertEquals(1, attempListener.invokeExecutorStartCount);
            Assert.assertEquals(1, attempListener.invokeExecutorEndCount);
            Assert.assertEquals(3, attempListener.invokeAfterMethodCount);
            Assert.assertEquals(3, attempListener.invokeOriginExceptionCount);
        }
    }

    static class CountAttemptListener implements ExecutorListener, InvokeListener {
        public int invokeExecutorStartCount;
        public int invokeAfterMethodCount;
        public int invokeOriginExceptionCount;
        public int invokeExecutorExceptionCount;
        public int invokeExecutorEndCount;
        private int invokeBeforeInvoke;

        @Override
        public void invokeExecutorStart(AttemptExecutor executor, AttemptContext context) {
            // 执行器开始的时候执行
            invokeExecutorStartCount++;
        }


        @Override
        public void invokeExecutorException(AttemptExecutor executor, AttemptContext context, Exception e) {
            // 执行器出现异常的时候执行
            invokeExecutorExceptionCount++;
        }

        @Override
        public void invokeExecutorEnd(AttemptExecutor executor, AttemptContext context) {
            // 执行器执行完毕后运行
            invokeExecutorEndCount++;
        }

        @Override
        public void invokeBeforeMethod(AttemptExecutor executor, AttemptContext context, TargetMethod method) {
            //真正方法调用前执行
            invokeBeforeInvoke++;
        }

        @Override
        public void invokeAfterMethod(AttemptExecutor executor, AttemptContext context, TargetMethod method, AttemptResult record) {
            // 真正方法调用之后执行
            invokeAfterMethodCount++;
        }

        @Override
        public void invokeOriginException(AttemptExecutor executor, AttemptContext context, TargetMethod method, Throwable e) {
            //真正方法 出现异常之后执行
            invokeOriginExceptionCount++;
        }
    }
}