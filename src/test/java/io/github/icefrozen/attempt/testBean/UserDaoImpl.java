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

package io.github.icefrozen.attempt.testBean;

import java.util.function.Consumer;

public class UserDaoImpl implements UserDao {
    private Consumer<String> checkListAllParameter = s -> {
    };
    private Consumer<String> checkListByIdParameter = s -> {
    };
    private Consumer<String> checkInsertParameter = s -> {
    };
    private Consumer<String> checkDeleteParameter = s -> {
    };

    @Override
    public void checkListAllParameter(Consumer<String> checkListAllParameter) {
        this.checkListAllParameter = checkListAllParameter;
    }

    @Override
    public void checkListByIdParameter(Consumer<String> checkListByIdParameter) {
        this.checkListByIdParameter = checkListByIdParameter;
    }

    @Override
    public void checkInsertParameter(Consumer<String> checkInsertParameter) {
        this.checkInsertParameter = checkInsertParameter;
    }

    @Override
    public void checkDeleteParameter(Consumer<String> checkDeleteParameter) {
        this.checkDeleteParameter = checkDeleteParameter;
    }

    @Override
    public String listAll(String s) {
        checkListAllParameter.accept(s);
        System.out.println("开始调用原始listAll方法");
        System.out.println("原始方法接收到的参数：" + s);
        System.out.println("结束调用原始listAll方法");
        return "listAll的返回值";
    }

    @Override
    public String listById(String s) {
        checkListByIdParameter.accept(s);
        System.out.println("开始调用原始listById方法");
        System.out.println("原始方法接收到的参数：" + s);
        System.out.println("结束调用原始listById方法");
        return "listById的返回值";
    }

    @Override
    public String insert(String s) {
        checkInsertParameter.accept(s);
        System.out.println("开始调用原始insert方法");
        System.out.println("原始方法接收到的参数：" + s);
        System.out.println("结束调用原始insert方法");
        return "insert的返回值";
    }

    @Override
    public String delete(String s) {
        checkDeleteParameter.accept(s);
        System.out.println("开始调用原始delete方法");
        System.out.println("原始方法接收到的参数：" + s);
        System.out.println("结束调用原始delete方法");
        return "delete的返回值";
    }
}
