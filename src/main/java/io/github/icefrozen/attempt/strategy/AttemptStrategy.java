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

package io.github.icefrozen.attempt.strategy;


import io.github.icefrozen.attempt.AttemptContext;
import io.github.icefrozen.attempt.AttemptResult;
import io.github.icefrozen.attempt.AttemptStatus;
import io.github.icefrozen.attempt.BaseAttemptPropertyViewer;

/**
 * 重试策略
 */
public interface AttemptStrategy {
    //1、是否结束
    boolean isEnd(AttemptContext context);
    // 一次调用声明周期结束后 true 进入循环，false 返回
    boolean back(AttemptResult record, AttemptContext context);
    // 策略名称
    String name();
    // 获取属性
    BaseAttemptPropertyViewer<? extends BaseAttemptPropertyViewer<?>> properties();
    // 获取状态
    AttemptStatus status();
    // 设置状态
    AttemptStatus status(AttemptStatus status);

}
