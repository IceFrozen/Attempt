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