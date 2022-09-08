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

package io.github.icefrozen.attempt.util;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 异常识别器
 *
 * @author Jason Lee
 */
public class ExceptionClassifier {
    /**
     * 如果traverseCauses为false，
     * 就简单调用父类进行转换即可，
     * 如果为真，就必须一直找Throwable的Cause链条，直到找到匹配的转换。
     */
    private boolean traverseCauses;
    // 异常识别策略
    public static final int exception_classifier_STRATEG = 0;

    private Map<Class<? extends Throwable>, Boolean> throwableMap = new ConcurrentHashMap<>();

    public ExceptionClassifier(boolean traverseCauses) {
        this.traverseCauses = traverseCauses;
    }

    public ExceptionClassifier() {
        this(false);
    }

    public ExceptionClassifier replyOn(Class<? extends Throwable> classifiable) {
        Boolean put = this.throwableMap.put(classifiable, Boolean.TRUE);
        return this;
    }

    public Class<?> classifyAndReturnClass(Throwable classifiable) {
        //判断异常类
        if(this.throwableMap.containsKey(classifiable.getClass())){
            return classifiable.getClass();
        };
        // 判断父类
        Class<?> isClass = this.superClassify(classifiable);
        if (isClass != null) {
            return isClass;
        }
        // 判断case链
        if (this.traverseCauses) {
            return this.superClassify(classifiable);
        }
        return null;
    }

    public boolean classify(Throwable classifiable) {
        return classifyAndReturnClass(classifiable) != null;
    }

    // 检查异常的父类是否在捕获范围之内
    public Class<?> superClassify(Throwable classifiable) {
        if (Objects.isNull(classifiable)) {
            return null;
        }
        Class<?> exceptionClass = classifiable.getClass().getSuperclass();
        if (this.throwableMap.containsKey(exceptionClass)) {
            return exceptionClass;
        }
        // 检查实体类
        for(Class<?> cls = exceptionClass; !cls.equals(Object.class); cls = cls.getSuperclass()) {
            boolean isClass = this.throwableMap.containsKey(cls);
            if (isClass) {
                return cls;
            }
            // 检查接口类
            for (Class<?> ifc : cls.getInterfaces()) {
                boolean isInterface = this.throwableMap.containsKey(ifc);
                if (isInterface) {
                    return ifc;
                }
            }
        }
        return null;
    }

    /**
     * 追踪链路
     */
    public boolean traverseCauses(Throwable classifiable) {
        Throwable cause = classifiable;
        if (Objects.isNull(classifiable)) {
            return false;
        }
        do {
            if (this.throwableMap.containsKey(cause.getClass())) {
                return true;
            }
            Class<?> aClass = this.superClassify(cause);
            if (aClass != null) {
                return true;
            }
            cause = cause.getCause();
        } while (cause != null);
        return false;
    }

    public void clean () {
        this.throwableMap.clear();
    }
}
