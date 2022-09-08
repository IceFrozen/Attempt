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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;

public class FixSizePriorityQueueTest {
    FixSizePriorityQueue<Integer> integers;
    @Before
    public void setUp () {
        integers =  new FixSizePriorityQueue<>(30, Comparator.comparingInt(o -> o));
        for (int i = 0; i < 30; i++) {
            integers.add(i);
        }
    }

    @Test
    public void FixSizePriorityQueueTest1() {
        for (int i = 31; i < 40 ; i++) {
            integers.add(i);
        }
        Assert.assertEquals(30, integers.size());
        Assert.assertEquals(true, integers.isIntact());

    }
    @Test
    public void testSmall() {
        FixSizePriorityQueue<Integer> mmHeap = new FixSizePriorityQueue<>(5, Comparator.comparingInt(o -> o));
        mmHeap.add(1);
        mmHeap.add(4);
        mmHeap.add(2);
        mmHeap.add(3);
        Assert.assertEquals(4, (int) mmHeap.pollLast());
        Assert.assertEquals(3, (int) mmHeap.peekLast());
        Assert.assertEquals(3, (int) mmHeap.pollLast());
        Assert.assertEquals(1, (int) mmHeap.peek());
        Assert.assertEquals(2, (int) mmHeap.peekLast());
        Assert.assertEquals(2, (int) mmHeap.pollLast());
        Assert.assertEquals(1, (int) mmHeap.peek());
        Assert.assertEquals(1, (int) mmHeap.peekLast());
        Assert.assertEquals(1, (int) mmHeap.pollLast());
        Assert.assertNull(mmHeap.peek());
        Assert.assertNull(mmHeap.peekLast());
        Assert.assertNull(mmHeap.pollLast());
    }
    @Test
    public void testSmallMinHeap() {
        FixSizePriorityQueue<Integer> mmHeap = new FixSizePriorityQueue<>(5, Comparator.comparingInt(o -> o));
        mmHeap.add(1);
        mmHeap.add(3);
        mmHeap.add(2);
        Assert.assertEquals(1, (int) mmHeap.peek());
        Assert.assertEquals(1, (int) mmHeap.poll());
        Assert.assertEquals(3, (int) mmHeap.peekLast());
        Assert.assertEquals(2, (int) mmHeap.peek());
        Assert.assertEquals(2, (int) mmHeap.poll());
        Assert.assertEquals(3, (int) mmHeap.peekLast());
        Assert.assertEquals(3, (int) mmHeap.peek());
        Assert.assertEquals(3, (int) mmHeap.poll());
        Assert.assertNull(mmHeap.peekLast());
        Assert.assertNull(mmHeap.peek());
        Assert.assertNull(mmHeap.poll());
    }
}