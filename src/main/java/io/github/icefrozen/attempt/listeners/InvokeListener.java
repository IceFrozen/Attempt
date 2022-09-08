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

package io.github.icefrozen.attempt.listeners;

import io.github.icefrozen.attempt.AttemptContext;
import io.github.icefrozen.attempt.AttemptExecutor;
import io.github.icefrozen.attempt.AttemptResult;
import io.github.icefrozen.attempt.proxy.TargetMethod;

/**
 * 方法调用监听器，该方法中如果抛出异常，则会执行执行器默认策略
 */
public interface InvokeListener extends AttemptListener {
    // 每一次实际调用前调用
    default void invokeBeforeMethod(AttemptExecutor executor, AttemptContext context, TargetMethod method) {};

    // 每一次实际调用后调用
    default void invokeAfterMethod(AttemptExecutor executor, AttemptContext context, TargetMethod method,
            AttemptResult record) {};

    // 每一次调用异常后调用
    default void invokeOriginException(AttemptExecutor executor, AttemptContext context, TargetMethod method,
            Throwable e) {};
}