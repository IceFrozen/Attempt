package io.github.icefrozen.attempt;

import io.github.icefrozen.attempt.testBean.ProxyDemo2;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;


public class AttemptPollingTest {

    @Before
    public void before() {
        ProxyDemo2.field5 = 0;
    }
    @After
    public void after() {
        ProxyDemo2.field5 = 0;
    }


    @Test
    public void testPollingSimple() {
        // 创建需要重试的对象
        ProxyDemo2 proxyDemo2 = new ProxyDemo2();
        // 构建重试代理
        AttemptBuilder.Polling<ProxyDemo2> pollingBuilder = new AttemptBuilder.Polling<>(proxyDemo2);
        ProxyDemo2 proxy = pollingBuilder
                .endPoint((context -> {
                    AttemptResult lastResult = context.getLastResult();
                    Integer retValue = (Integer) lastResult.getRetValue();
                    return retValue == 3;
                }))
                .build();
        for (int i = 0; i < 10; i++) {
            int r = proxy.plusStaticCount();
            Assert.assertEquals(3, ProxyDemo2.field5);
            Assert.assertEquals(3, r);
            ProxyDemo2.field5 = 0;
        }
    }

    /**
     * 4 5 6 报错 重试三次 连续3次异常 则退出
     */
    @Test
    public void testPollingTreeExceptionSimple() {
        // 456 报错
        ProxyDemo2 proxyDemo2 = new ProxyDemo2(Stream.of(4, 5, 6).collect(Collectors.toList()));
        // 构建重试代理
        AttemptBuilder.Polling<ProxyDemo2> pollingBuilder = new AttemptBuilder.Polling<>(proxyDemo2);
        ProxyDemo2 proxy = pollingBuilder
                .endPoint((context -> {
                    AttemptResult lastResult = context.getLastResult();
                    Integer retValue = (Integer) lastResult.getRetValue();
                    return retValue == 100;
                }))
                .build();
        try {
            int i = proxy.plusStaticThreeCount();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertEquals(6, proxyDemo2.getField1());
        }
    }

    @Test
    public void testPollingTreeExceptionNotRangeSimple() {
        // 4, 5, 7, 11, 12, 13 报错
        // 45 抛异常 因此不连续3 次，则重置 7 只有一次异常  11 12 13 三次连续异常 因此proxyDemo2 plusStaticThreeCount 执行了13 次
        ProxyDemo2 proxyDemo2 = new ProxyDemo2(Stream.of(4, 5, 7, 11, 12, 13).collect(Collectors.toList()));

        // 构建重试代理
        AttemptBuilder.Polling<ProxyDemo2> pollingBuilder = new AttemptBuilder.Polling<>(proxyDemo2);
        ProxyDemo2 proxy = pollingBuilder
                .endPoint((context -> {
                    AttemptResult lastResult = context.getLastResult();
                    Integer retValue = (Integer) lastResult.getRetValue();
                    return retValue == 100;
                }))
                .registerExceptionRetryTime(RuntimeException.class, 3)      //遇到该异常重试3次
                //.backOff(FixedSleepingBackOffPolicy.class)   // 每一次请求的最小时间限制
                .retryMax(100)     // 最大轮询次数， -1 为不限制 不限制有死循环的风险，TODO 因此需要加timeout
                .build();

        try {
            // plusStaticCount 的结果 必须为3 才停止，
            int i = proxy.plusStaticThreeCount();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof RuntimeException);
            // 45 抛异常 因此不连续3 次，则重置 7 只有一次异常  11 12 13 三次连续异常 因此proxyDemo2 plusStaticThreeCount 执行了13 次
            Assert.assertEquals(13, proxyDemo2.getField1());
        }
    }

    @Test
    public void testPollingInvokerSimple() {
        // 创建需要重试的对象
        ProxyDemo2 proxyDemo2 = new ProxyDemo2();
        // 构建重试代理
        Integer exec = AttemptBuilder.polling(() -> proxyDemo2.plusStaticCount()).endPoint((context -> {
            AttemptResult lastResult = context.getLastResult();
            Integer retValue = (Integer) lastResult.getRetValue();
            return retValue == 3;
        })).exec();

        Assert.assertEquals(3, ProxyDemo2.field5);
        Assert.assertEquals((int)exec, ProxyDemo2.field5);
        ProxyDemo2.field5 = 0;

    }

    @Test
    public void testPollingInvokerSimpleLoop() {
        for (int i = 0; i < 12; i++) {
            // 创建需要重试的对象
            ProxyDemo2 proxyDemo2 = new ProxyDemo2();
            // 构建重试代理
            AttemptBuilder.polling(proxyDemo2::plusStaticCount).endPoint((context -> {
                AttemptResult lastResult = context.getLastResult();
                Integer retValue = (Integer) lastResult.getRetValue();
                return retValue == 3;
            })).exec();
            Assert.assertEquals(3, ProxyDemo2.field5);
            ProxyDemo2.field5 = 0;
        }
    }

    @Test
    public void testPollingMapxPollTime() {
        // 4, 5, 7, 11, 12, 13 报错
        // 45 抛异常 因此不连续3 次，则重置 7 只有一次异常  11 12 13 三次连续异常 因此proxyDemo2 plusStaticThreeCount 执行了13 次
        ProxyDemo2 proxyDemo2 = new ProxyDemo2(Stream.of(4, 5, 7, 11, 12, 13).collect(Collectors.toList()));

        // 构建重试代理
        AttemptBuilder.Polling<ProxyDemo2> pollingBuilder = new AttemptBuilder.Polling<>(proxyDemo2);
        ProxyDemo2 proxy = pollingBuilder
                .endPoint((context -> {
                    AttemptResult lastResult = context.getLastResult();
                    Integer retValue = (Integer) lastResult.getRetValue();
                    return retValue == 100;
                }))
                .maxPollCount(3)
                .registerExceptionRetryTime(RuntimeException.class, 3)      //遇到该异常重试3次
                //.backOff(FixedSleepingBackOffPolicy.class)   // 每一次请求的最小时间限制
                .retryMax(-1)     // 最大轮询次数， -1 为不限制 不限制有死循环的风险，TODO 因此需要加timeout
                .build();
        try {
            // plusStaticCount 的结果 必须为3 才停止，
            int i = proxy.plusStaticThreeCount();
            Assert.assertEquals(3, proxyDemo2.getField1());
        } catch (Exception e) {
            System.out.println(proxyDemo2.getField1());
            Assert.assertTrue(e instanceof RuntimeException);
            // 45 抛异常 因此不连续3 次，则重置 7 只有一次异常  11 12 13 三次连续异常 因此proxyDemo2 plusStaticThreeCount 执行了13 次
            Assert.assertEquals(13, proxyDemo2.getField1());
        }

    }

    @Test
    public void testPollingMapxPollTime2() {
        // 4, 5, 7, 11, 12, 13 报错
        // 45 抛异常 因此不连续3 次，则重置 7 只有一次异常  11 12 13 三次连续异常 因此proxyDemo2 plusStaticThreeCount 执行了13 次
        ProxyDemo2 proxyDemo2 = new ProxyDemo2(Stream.of(4, 5, 7, 11, 12, 13).collect(Collectors.toList()));

        // 构建重试代理
        AttemptBuilder.Polling<ProxyDemo2> pollingBuilder = new AttemptBuilder.Polling<>(proxyDemo2);
        ProxyDemo2 proxy = pollingBuilder
                .endPoint((context -> {
                    AttemptResult lastResult = context.getLastResult();
                    Integer retValue = (Integer) lastResult.getRetValue();
                    return retValue == 100;
                }))
                .maxPollCount(6)
                .registerExceptionRetryTime(RuntimeException.class, 3)      //遇到该异常重试3次
                //.backOff(FixedSleepingBackOffPolicy.class)   // 每一次请求的最小时间限制
                .retryMax(2)     // 最大轮询次数， -1 为不限制 不限制有死循环的风险，TODO 因此需要加timeout
                .build();
        try {
            // plusStaticCount 的结果 必须为3 才停止，
            int i = proxy.plusStaticThreeCount();
        } catch (Exception e) {
            System.out.println(proxyDemo2.getField1());
            Assert.assertTrue(e instanceof RuntimeException);
            // 45 抛异常 因此不连续3 次，则重置 7 只有一次异常  11 12 13 三次连续异常 因此proxyDemo2 plusStaticThreeCount 执行了13 次
            Assert.assertEquals(5, proxyDemo2.getField1());
        }

    }

    @Test
    public void testPollingMapxPollTime3() {
        // 4, 5, 7, 11, 12, 13 报错
        // 45 抛异常 因此不连续3 次，则重置 7 只有一次异常  11 12 13 三次连续异常 因此proxyDemo2 plusStaticThreeCount 执行了13 次
        ProxyDemo2 proxyDemo2 = new ProxyDemo2(Stream.of(4, 5, 7, 11, 12, 13).collect(Collectors.toList()));

        // 构建重试代理
        AttemptBuilder.Polling<ProxyDemo2> pollingBuilder = new AttemptBuilder.Polling<>(proxyDemo2);
        ProxyDemo2 proxy = pollingBuilder
                .endPoint((context -> {
                    AttemptResult lastResult = context.getLastResult();
                    Integer retValue = (Integer) lastResult.getRetValue();
                    return retValue == 100;
                }))
                .maxPollCount(6)
                .registerExceptionRetryTime(RuntimeException.class, 3)      //遇到该异常重试3次
                //.backOff(FixedSleepingBackOffPolicy.class)   // 每一次请求的最小时间限制
                .retryMax(3)     // 最大轮询次数， -1 为不限制 不限制有死循环的风险，TODO 因此需要加timeout
                .build();
        try {
            // plusStaticCount 的结果 必须为3 才停止，
            int i = proxy.plusStaticThreeCount();
            Assert.assertEquals(6, proxyDemo2.getField1());
        } catch (Exception e) {
            Assert.assertTrue(e instanceof RuntimeException);
            // 45 抛异常 因此不连续3 次，则重置 7 只有一次异常  11 12 13 三次连续异常 因此proxyDemo2 plusStaticThreeCount 执行了13 次
            Assert.assertEquals(5, proxyDemo2.getField1());
        }

    }
}
