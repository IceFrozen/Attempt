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

/**
 * 执行器监听器，如果实行该方法内抛出异常，
 * 则不会被重试策略捕获，而是直接抛出
 */
public interface ExecutorListener extends AttemptListener {
    // 执行器开始前调用
    default void invokeExecutorStart(AttemptExecutor executor, AttemptContext context) {};

    //执行器异常时执行
    default void invokeExecutorException(AttemptExecutor executor, AttemptContext context, Exception e) {};

    // 执行器结束后触发
    default void invokeExecutorEnd(AttemptExecutor executor, AttemptContext context) {};
}
