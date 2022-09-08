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

package io.github.icefrozen.attempt.proxy;

import io.github.icefrozen.attempt.testBean.*;
import io.github.icefrozen.attempt.util.Convert;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.Arrays;

public class ProxyUtilsTest {
    static IProxyDemo iProxyDemo;
    static ProxyDemo2 proxyDemo2;
    static TestBean testBean;

    static MethodInterceptor interceptor;
    static MethodInterceptor interceptorWhen;


    @Before
    public void before() {
        iProxyDemo = new ProxyDemoImpl();
        proxyDemo2 = new ProxyDemo2();
        testBean = new TestBean();
        interceptor = targetMethod -> {
            MethodSignature signature = targetMethod.getSignature();
            Object[] params = targetMethod.getParams();
            System.out.println(params.length);
            System.out.println("开始拦截" + signature.getName() + "方法");
            Object ret = targetMethod.invoke(params);
            System.out.println("结束拦截" + signature.getName() + "方法");
            return "proxy:" + ret;
        };

        interceptorWhen = ((MethodInterceptor) targetMethod -> {
            MethodSignature signature = targetMethod.getSignature();
            Object[] params = targetMethod.getParams();
            System.out.println(params.length);
            System.out.println("开始拦截" + signature.getName() + "方法");
            Object ret = targetMethod.invoke(params);
            System.out.println("结束拦截" + signature.getName() + "方法 ret:" + ret);
            return Convert.toInt(ret) + 1;
        }).when(MethodMatcher.withPattern("get.*").andReturnType(int.class));

    }

    @Test
    public void jdkProxyTest() {
        // 接口拦截
        IProxyDemo proxy = ProxyUtils.proxy(iProxyDemo, interceptor);
        Assert.assertTrue(Proxy.isProxyClass(proxy.getClass()));

        String s = proxy.proxyMethod("1,2,3");
        Assert.assertTrue(StringUtils.startsWith(s, "proxy"));
        // 方法拦截
        ProxyDemo2 proxy2 = ProxyUtils.proxy(proxyDemo2, interceptorWhen);
        proxy2.setField1(1);
        int field1 = proxy2.getField1();
        Assert.assertEquals(2, field1);

    }

    @Test
    public void cglibProxyTest() {
        // 接口拦截
        TestBean proxy1 = ProxyUtils.proxy(testBean, interceptor);
        Assert.assertTrue(ProxyUtils.isProxy(proxy1.getClass()));
        Assert.assertFalse(ProxyUtils.isProxy(TestBean.class));

        IProxyDemo proxy2 = ProxyUtils.proxy(iProxyDemo, interceptor);
        Assert.assertTrue(ProxyUtils.isProxy(proxy2.getClass()));
        Assert.assertFalse(ProxyUtils.isProxy(IProxyDemo.class));
    }

    @Test
    public void implementTest() {
        IProxyDemo implement = ProxyUtils.implement(IProxyDemo.class, new MethodInterceptor() {
            @Override
            public Object intercept(TargetMethod targetMethod) {
                return targetMethod.invokeWithOriginalParams();
            }
        });
        String aaaa = implement.proxyMethod("default method invoke");
        Assert.assertEquals("default method invoke", aaaa);
        IProxyDemo implement2 = ProxyUtils.implement(IProxyDemo.class, MethodInterceptor.delegateTo(new IProxyDemo() {
            @Override
            public String proxyMethod(Object... args) {
                String join = StringUtils.join(args);
                Assert.assertEquals("12", join);
                return "i am ok";
            }
        }));
        String IamOk = implement2.proxyMethod("1", "2");
        Assert.assertEquals("i am ok", IamOk);
    }

    public static class MyInterceptor implements MethodInterceptor {
        private final String name;

        public MyInterceptor(String name) {
            this.name = name;
        }

        @Override
        public Object intercept(TargetMethod targetMethod) {
            MethodSignature signature = targetMethod.getSignature();
            Object[] params = targetMethod.getParams();
            System.out.println(name + ": 开始拦截" + signature.getName() + "方法");
            System.out.println(name + ": 原始参数：" + Arrays.toString(params));
            Object ret = targetMethod.invoke(params[0] + " " + name);
            System.out.println(name + ": 原始返回值：" + ret);
            System.out.println(name + ": 结束拦截" + signature.getName() + "方法");
            return ret + " " + name;
        }
    }

    private final MyInterceptor interceptor1 = new MyInterceptor("interceptor1");
    private final MyInterceptor interceptor2 = new MyInterceptor("interceptor2");

    @Test
    public void whenTest1() {
        UserDao userDao = ProxyUtils.proxy(new UserDaoImpl(), interceptor1.when(MethodMatcher.withParameterTypes(String.class)));
        Assert.assertTrue(Proxy.isProxyClass(userDao.getClass()));

        userDao.checkListAllParameter(s -> Assert.assertEquals("listAll的参数 interceptor1", s));
        userDao.checkListByIdParameter(s -> Assert.assertEquals("listById的参数 interceptor1", s));
        userDao.checkInsertParameter(s -> Assert.assertEquals("insert的参数 interceptor1", s));
        userDao.checkDeleteParameter(s -> Assert.assertEquals("delete的参数 interceptor1", s));

        Assert.assertEquals("listAll的返回值 interceptor1", userDao.listAll("listAll的参数"));
        Assert.assertEquals("listById的返回值 interceptor1", userDao.listById("listById的参数"));
        Assert.assertEquals("insert的返回值 interceptor1", userDao.insert("insert的参数"));
        Assert.assertEquals("delete的返回值 interceptor1", userDao.delete("delete的参数"));
    }

    @Test
    public void whenTest2() {
        UserDao userDao = ProxyUtils.proxy(new UserDaoImpl(), interceptor1.when(MethodMatcher.withName("listAll")));

        userDao.checkListAllParameter(s -> Assert.assertEquals("listAll的参数 interceptor1", s));
        userDao.checkListByIdParameter(s -> Assert.assertEquals("listById的参数", s));
        userDao.checkInsertParameter(s -> Assert.assertEquals("insert的参数", s));
        userDao.checkDeleteParameter(s -> Assert.assertEquals("delete的参数", s));

        Assert.assertEquals("listAll的返回值 interceptor1", userDao.listAll("listAll的参数"));
        Assert.assertEquals("listById的返回值", userDao.listById("listById的参数"));
        Assert.assertEquals("insert的返回值", userDao.insert("insert的参数"));
        Assert.assertEquals("delete的返回值", userDao.delete("delete的参数"));
    }

    @Test
    public void whenTes3t() {
        UserDao userDao = ProxyUtils.proxy(new UserDaoImpl(), interceptor1.when(MethodMatcher.withName("listAll").or(MethodMatcher.withName("insert"))));

        userDao.checkListAllParameter(s -> Assert.assertEquals("listAll的参数 interceptor1", s));
        userDao.checkListByIdParameter(s -> Assert.assertEquals("listById的参数", s));
        userDao.checkInsertParameter(s -> Assert.assertEquals("insert的参数 interceptor1", s));
        userDao.checkDeleteParameter(s -> Assert.assertEquals("delete的参数", s));

        Assert.assertEquals("listAll的返回值 interceptor1", userDao.listAll("listAll的参数"));
        Assert.assertEquals("listById的返回值", userDao.listById("listById的参数"));
        Assert.assertEquals("insert的返回值 interceptor1", userDao.insert("insert的参数"));
        Assert.assertEquals("delete的返回值", userDao.delete("delete的参数"));
    }

    @Test
    public void whenTest4() {
        UserDao userDao = ProxyUtils.proxy(new UserDaoImpl(), interceptor1.then(interceptor2).when(MethodMatcher.withParameterTypes(String.class)));

        userDao.checkListAllParameter(s -> Assert.assertEquals("listAll的参数 interceptor2 interceptor1", s));
        userDao.checkListByIdParameter(s -> Assert.assertEquals("listById的参数 interceptor2 interceptor1", s));
        userDao.checkInsertParameter(s -> Assert.assertEquals("insert的参数 interceptor2 interceptor1", s));
        userDao.checkDeleteParameter(s -> Assert.assertEquals("delete的参数 interceptor2 interceptor1", s));

        Assert.assertEquals("listAll的返回值 interceptor1 interceptor2", userDao.listAll("listAll的参数"));
        Assert.assertEquals("listById的返回值 interceptor1 interceptor2", userDao.listById("listById的参数"));
        Assert.assertEquals("insert的返回值 interceptor1 interceptor2", userDao.insert("insert的参数"));
        Assert.assertEquals("delete的返回值 interceptor1 interceptor2", userDao.delete("delete的参数"));
    }

    @Test
    public void whenTest5() {
        UserDao userDao = ProxyUtils.proxy(new UserDaoImpl(), interceptor1.when(MethodMatcher.withName("listById")).then(interceptor2.when(MethodMatcher.withName("delete"))));

        userDao.checkListAllParameter(s -> Assert.assertEquals("listAll的参数", s));
        userDao.checkListByIdParameter(s -> Assert.assertEquals("listById的参数 interceptor1", s));
        userDao.checkInsertParameter(s -> Assert.assertEquals("insert的参数", s));
        userDao.checkDeleteParameter(s -> Assert.assertEquals("delete的参数 interceptor2", s));

        Assert.assertEquals("listAll的返回值", userDao.listAll("listAll的参数"));
        Assert.assertEquals("listById的返回值 interceptor1", userDao.listById("listById的参数"));
        Assert.assertEquals("insert的返回值", userDao.insert("insert的参数"));
        Assert.assertEquals("delete的返回值 interceptor2", userDao.delete("delete的参数"));
    }

    @Test
    public void testStatic() {
        /**
         * 开箱即用
         */


    }
}