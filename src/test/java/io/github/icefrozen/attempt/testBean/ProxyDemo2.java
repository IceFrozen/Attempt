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


import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ProxyDemo2 {
    public int field1;
    public int field2;

    public static int field5;

    public String field3;
    public List<Integer> field4;
    List<Integer> errorNumber;

    public ProxyDemo2(List<Integer> errorNumber) {
        this.errorNumber = errorNumber;
    }
    public ProxyDemo2() {
        this.errorNumber = new ArrayList<>();
    }

    public int count = 0;
    public int getField1() {
        count++;
        return field1;
    }

    public int errorMethod() {
        count +=1;
        int i = 1/0;
        return 100;
    }
    public static int staticErrorMethod() {
        field5 +=1;
        int i = 1/0;
        return 100;
    }

    public static  int getFileStatic () {
        field5 ++;
        return field5;
    }

    public static void getVoid () {
        getFileStatic();
    }

    public static void getVoidError () {
        staticErrorMethod();
    }


    public int plusStaticThreeCount () {
        field1 ++;
        if(errorNumber.indexOf(field1) > -1) {
            throw new RuntimeException("error of number:" + field1);
        }
        return field1;
    }

    public synchronized int plusStaticCount () {
        field5 ++;
        if (ProxyDemo2.this.field5 > 3) {
            int i = 0;
        }
        return field5;
    }



    public void setField1(int field1) {
        this.field1 = field1;
    }

    public int getField2() {
        return field2;
    }

    public void setField2(int field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public List<Integer> getField4() {
        return field4;
    }

    public void setField4(List<Integer> field4) {
        this.field4 = field4;
    }
    public String proxyMethod(Object... args) {
        String retVal = "ProxyDemo2 ... args:" + StringUtils.join(args);
        System.out.println(retVal);
        return retVal;
    }
}
