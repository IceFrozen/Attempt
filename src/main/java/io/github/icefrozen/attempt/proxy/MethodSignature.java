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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * 方法签名
 */
public interface MethodSignature {
    /**
     * 获取方法名
     */
    String getName();

    /**
     * 获取返回值类型
     */
    Class<?> getReturnType();

    /**
     * 获取泛型返回类型
     */
    Type getGenericReturnType();

    /**
     * 获取参数类型
     */
    Class<?>[] getParameterTypes();

    /**
     * 获取参数名
     */
    String[] getParameterNames();

    /**
     * 获取方法上的指定注解
     */
    <T extends Annotation> T getAnnotation(Class<T> annotationClass);

    /**
     * 获取方法上的所有注解
     */
    Annotation[] getAnnotations();

    /**
     * 方法是否被某个注解标注
     */
    <T extends Annotation> boolean hasAnnotation(Class<T> annotationClass);

    /**
     * 获取方法参数上的注解
     */
    Annotation[][] getParameterAnnotations();

    /**
     * 是否为public方法
     */
    boolean isPublic();

    /**
     * 是否为private方法
     */
    boolean isPrivate();

    /**
     * 是否为protected方法
     */
    boolean isProtected();

    /**
     * 获取方法对象
     */
    Method getMethod();

    /**
     * 创建MethodSignature
     */
    static MethodSignature of(Method method) {
        return new MethodSignature() {
            @Override
            public String getName() {
                return method.getName();
            }

            @Override
            public Class<?> getReturnType() {
                return method.getReturnType();
            }

            @Override
            public Type getGenericReturnType() {
                return method.getGenericReturnType();
            }

            @Override
            public Class<?>[] getParameterTypes() {
                return method.getParameterTypes();
            }

            @Override
            public String[] getParameterNames() {
                Parameter[] parameters = method.getParameters();
                return Arrays.stream(parameters).map(Parameter::getName).toArray(String[]::new);
            }

            @Override
            public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
                return method.getAnnotation(annotationClass);
            }

            @Override
            public Annotation[] getAnnotations() {
                return method.getAnnotations();
            }

            @Override
            public <T extends Annotation> boolean hasAnnotation(Class<T> annotationClass) {
                return method.isAnnotationPresent(annotationClass);
            }

            @Override
            public Annotation[][] getParameterAnnotations() {
                return method.getParameterAnnotations();
            }

            @Override
            public boolean isPublic() {
                return Modifier.isPublic(method.getModifiers());
            }

            @Override
            public boolean isPrivate() {
                return Modifier.isPrivate(method.getModifiers());
            }

            @Override
            public boolean isProtected() {
                return Modifier.isProtected(method.getModifiers());
            }

            @Override
            public Method getMethod() {
                return method;
            }
        };
    }
}
