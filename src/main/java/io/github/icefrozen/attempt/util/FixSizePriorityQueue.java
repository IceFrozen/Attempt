package io.github.icefrozen.attempt.util;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * 固定大小优先级队列
 * @param <E>
 */
public final class FixSizePriorityQueue<E> extends AbstractQueue<E> {
    private final Heap minHeap;     // 小端堆
    private final Heap maxHeap;     // 大端堆
    private final int fixedSizs;          // 细小
    private Object[] queue;
    private int size;
    private int modCount;
    // 默认期望大小
    private static final int DEFAULTEXPECTEDSIZE = 11;

    public FixSizePriorityQueue(int size, Comparator<E> comparator) {
        this.minHeap = new Heap(comparator);
        this.maxHeap = new Heap(comparator.reversed());
        minHeap.otherHeap = maxHeap;
        maxHeap.otherHeap = minHeap;

        this.fixedSizs = size;
        this.queue = new Object[initialQueueSize(size)];
    }

    private int initialQueueSize(int fixedSize) {
        // 兼容单一情况
        if (fixedSize == 1) {
            return fixedSize + 1;
        }

        return capAtMaximumSize(DEFAULTEXPECTEDSIZE, fixedSize);
    }

    /**
     * 动态扩缩绒
     */
    private void growSize() {
        if (size > queue.length) {
            int newCapacity = calculateNewCapacity();
            Object[] newQueue = new Object[newCapacity];
            System.arraycopy(queue, 0, newQueue, 0, queue.length);
            queue = newQueue;
        }
    }

    /**
     * 计算容量
     */
    private int calculateNewCapacity() {
        int oldCapacity = queue.length;
        // 2的某次方
        int newCapacity =
                (oldCapacity < 64) ? (oldCapacity + 1) * 2 : Convert.toInt(oldCapacity / 2 * 3);;
        return capAtMaximumSize(newCapacity, fixedSizs);
    }

    private static int capAtMaximumSize(int queueSize, int maximumSize) {
        return Math.min(queueSize - 1, maximumSize) + 1;
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * 往队列里增加元素
     * 如果元素数量大于指定元素
     * 则自动移除比较器顶级元素.
     */
    @Override
    public boolean add(E element) {
        offer(element);
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> newElements) {
        boolean modified = false;
        for (E element : newElements) {
            offer(element);
            modified = true;
        }
        return modified;
    }


    @Override
    public boolean offer(E element) {
        modCount++;
        int insertIndex = size++;
        growSize();
        heapForIndex(insertIndex).bubbleUp(insertIndex, element);
        return size <= fixedSizs || pollLast() != element;
    }


    @Override
    public E poll() {
        return isEmpty() ? null : removeAndGet(0);
    }

    @SuppressWarnings("unchecked")
    E elementData(int index) {
        return (E) requireNonNull(queue[index]);
    }

    @Override
    public E peek() {
        return isEmpty() ? null : elementData(0);
    }

    /**
     * 最大路径index
     */
    private int getMaxElementIndex() {
        switch (size) {
            case 1:
                return 0;
            case 2:
                return 1;
            default:
                return (maxHeap.compareElements(1, 2) <= 0) ? 1 : 2;
        }
    }

    public E pollFirst() {
        return poll();
    }

    public E removeFirst() {
        return remove();
    }

    public E peekFirst() {
        return peek();
    }

    public E pollLast() {
        return isEmpty() ? null : removeAndGet(getMaxElementIndex());
    }

    public E removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return removeAndGet(getMaxElementIndex());
    }

    public E peekLast() {
        return isEmpty() ? null : elementData(getMaxElementIndex());
    }

    private MoveDesc<E> removeAt(int index) {
        modCount++;
        size--;
        if (size == index) {
            queue[size] = null;
            return null;
        }
        E actualLastElement = elementData(size);
        int lastElementAt = heapForIndex(size).swapWithConceptuallyLastElement(actualLastElement);
        if (lastElementAt == index) {
            // 'actualLastElement' is now at 'lastElementAt', and the element that was at 'lastElementAt'
            // is now at the end of queue. If that's the element we wanted to remove in the first place,
            // don't try to (incorrectly) trickle it. Instead, just delete it and we're done.
            queue[size] = null;
            return null;
        }
        E toTrickle = elementData(size);
        queue[size] = null;
        MoveDesc<E> changes = fillHole(index, toTrickle);
        if (lastElementAt < index) {
            // Last element is moved to before index, swapped with trickled element.
            if (changes == null) {
                // The trickled element is still after index.
                return new MoveDesc<>(actualLastElement, toTrickle);
            } else {
                // The trickled element is back before index, but the replaced element
                // has now been moved after index.
                return new MoveDesc<>(actualLastElement, changes.replaced);
            }
        }
        // Trickled element was after index to begin with, no adjustment needed.
        return changes;
    }


    private MoveDesc<E> fillHole(int index, E toTrickle) {
        Heap heap = heapForIndex(index);
        // We consider elementData(index) a "hole", and we want to fill it
        // with the last element of the heap, toTrickle.
        // Since the last element of the heap is from the bottom level, we
        // optimistically fill index position with elements from lower levels,
        // moving the hole down. In most cases this reduces the number of
        // comparisons with toTrickle, but in some cases we will need to bubble it
        // all the way up again.
        int vacated = heap.fillHoleAt(index);
        // Try to see if toTrickle can be bubbled up min levels.
        int bubbledTo = heap.bubbleUpAlternatingLevels(vacated, toTrickle);
        if (bubbledTo == vacated) {
            // Could not bubble toTrickle up min levels, try moving
            // it from min level to max level (or max to min level) and bubble up
            // there.
            return heap.tryCrossOverAndBubbleUp(index, vacated, toTrickle);
        } else {
            return (bubbledTo < index) ? new MoveDesc<E>(toTrickle, elementData(index)) : null;
        }
    }

    // Returned from removeAt() to iterator.remove()
    static class MoveDesc<E> {
        final E toTrickle;
        final E replaced;

        MoveDesc(E toTrickle, E replaced) {
            this.toTrickle = toTrickle;
            this.replaced = replaced;
        }
    }

    /**
     * Removes and returns the value at {@code index}.
     */
    private E removeAndGet(int index) {
        E value = elementData(index);
        removeAt(index);
        return value;
    }
    // 奇偶树层次选择
    private Heap heapForIndex(int i) {
        return isEvenLevel(i) ? minHeap : maxHeap;
    }

    private static final int EVEN_POWERS_OF_TWO = 0x55555555;
    private static final int ODD_POWERS_OF_TWO = 0xaaaaaaaa;

    // 奇偶数层次判定
    public static boolean isEvenLevel(int index) {
        int oneBased = ~~(index + 1); // for GWT
        return (oneBased & EVEN_POWERS_OF_TWO) > (oneBased & ODD_POWERS_OF_TWO);
    }


    boolean isIntact() {
        for (int i = 1; i < size; i++) {
            if (!heapForIndex(i).verifyIndex(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 驱动堆
     */
    private class Heap {
        final Comparator<E> ordering;
        Heap otherHeap;
        Heap(Comparator<E> ordering) {
            this.ordering = ordering;
        }
        int compareElements(int a, int b) {
            return ordering.compare(elementData(a), elementData(b));
        }

        /**
         * Tries to move {@code toTrickle} from a min to a max level and bubble up there. If it moved
         * before {@code removeIndex} this method returns a pair as described in {@link #removeAt}.
         */

        MoveDesc<E> tryCrossOverAndBubbleUp(int removeIndex, int vacated, E toTrickle) {
            int crossOver = crossOver(vacated, toTrickle);
            if (crossOver == vacated) {
                return null;
            }
            // Successfully crossed over from min to max.
            // Bubble up max levels.
            E parent;
            // If toTrickle is moved up to a parent of removeIndex, the parent is
            // placed in removeIndex position. We must return that to the iterator so
            // that it knows to skip it.
            if (crossOver < removeIndex) {
                // We crossed over to the parent level in crossOver, so the parent
                // has already been moved.
                parent = elementData(removeIndex);
            } else {
                parent = elementData(getParentIndex(removeIndex));
            }
            // bubble it up the opposite heap
            if (otherHeap.bubbleUpAlternatingLevels(crossOver, toTrickle) < removeIndex) {
                return new MoveDesc<>(toTrickle, parent);
            } else {
                return null;
            }
        }

        /**
         * 动态调整堆顺序
         */
        void bubbleUp(int index, E x) {
            int crossOver = crossOverUp(index, x);

            Heap heap;
            if (crossOver == index) {
                heap = this;
            } else {
                index = crossOver;
                heap = otherHeap;
            }
            heap.bubbleUpAlternatingLevels(index, x);
        }

        /**
         * Bubbles a value from {@code index} up the levels of this heap, and returns the index the
         * element ended up at.
         */

        int bubbleUpAlternatingLevels(int index, E x) {
            while (index > 2) {
                int grandParentIndex = getGrandparentIndex(index);
                E e = elementData(grandParentIndex);
                if (ordering.compare(e, x) <= 0) {
                    break;
                }
                queue[index] = e;
                index = grandParentIndex;
            }
            queue[index] = x;
            return index;
        }

        /**
         * Returns the index of minimum value between {@code index} and {@code index + len}, or {@code
         * -1} if {@code index} is greater than {@code size}.
         */
        int findMin(int index, int len) {
            if (index >= size) {
                return -1;
            }
            int limit = Math.min(index, size - len) + len;
            int minIndex = index;
            for (int i = index + 1; i < limit; i++) {
                if (compareElements(i, minIndex) < 0) {
                    minIndex = i;
                }
            }
            return minIndex;
        }

        /**
         * Returns the minimum child or {@code -1} if no child exists.
         */
        int findMinChild(int index) {
            return findMin(getLeftChildIndex(index), 2);
        }

        /**
         * Returns the minimum grand child or -1 if no grand child exists.
         */
        int findMinGrandChild(int index) {
            int leftChildIndex = getLeftChildIndex(index);
            if (leftChildIndex < 0) {
                return -1;
            }
            return findMin(getLeftChildIndex(leftChildIndex), 4);
        }

        /**
         * Moves an element one level up from a min level to a max level (or vice versa). Returns the
         * new position of the element.
         */
        int crossOverUp(int index, E x) {
            if (index == 0) {
                queue[0] = x;
                return 0;
            }
            int parentIndex = getParentIndex(index);
            E parentElement = elementData(parentIndex);
            if (parentIndex != 0) {
                // This is a guard for the case of the childless uncle.
                // Since the end of the array is actually the middle of the heap,
                // a smaller childless uncle can become a child of x when we
                // bubble up alternate levels, violating the invariant.
                int grandparentIndex = getParentIndex(parentIndex);
                int uncleIndex = getRightChildIndex(grandparentIndex);
                if (uncleIndex != parentIndex && getLeftChildIndex(uncleIndex) >= size) {
                    E uncleElement = elementData(uncleIndex);
                    if (ordering.compare(uncleElement, parentElement) < 0) {
                        parentIndex = uncleIndex;
                        parentElement = uncleElement;
                    }
                }
            }
            // 父节点是0 号位置 交换或者直接放入
            if (ordering.compare(parentElement, x) < 0) {
                queue[index] = parentElement;
                queue[parentIndex] = x;
                return parentIndex;
            }
            queue[index] = x;
            return index;
        }

        /**
         * Swap {@code actualLastElement} with the conceptually correct last element of the heap.
         * Returns the index that {@code actualLastElement} now resides in.
         *
         * <p>Since the last element of the array is actually in the middle of the sorted structure, a
         * childless uncle node could be smaller, which would corrupt the invariant if this element
         * becomes the new parent of the uncle. In that case, we first switch the last element with its
         * uncle, before returning.
         */
        int swapWithConceptuallyLastElement(E actualLastElement) {
            int parentIndex = getParentIndex(size);
            if (parentIndex != 0) {
                int grandparentIndex = getParentIndex(parentIndex);
                int uncleIndex = getRightChildIndex(grandparentIndex);
                if (uncleIndex != parentIndex && getLeftChildIndex(uncleIndex) >= size) {
                    E uncleElement = elementData(uncleIndex);
                    if (ordering.compare(uncleElement, actualLastElement) < 0) {
                        queue[uncleIndex] = actualLastElement;
                        queue[size] = uncleElement;
                        return uncleIndex;
                    }
                }
            }
            return size;
        }

        /**
         * Crosses an element over to the opposite heap by moving it one level down (or up if there are
         * no elements below it).
         *
         * <p>Returns the new position of the element.
         */
        int crossOver(int index, E x) {
            int minChildIndex = findMinChild(index);
            // TODO(kevinb): split the && into two if's and move crossOverUp so it's
            // only called when there's no child.
            if ((minChildIndex > 0) && (ordering.compare(elementData(minChildIndex), x) < 0)) {
                queue[index] = elementData(minChildIndex);
                queue[minChildIndex] = x;
                return minChildIndex;
            }
            return crossOverUp(index, x);
        }

        /**
         * Fills the hole at {@code index} by moving in the least of its grandchildren to this position,
         * then recursively filling the new hole created.
         *
         * @return the position of the new hole (where the lowest grandchild moved from, that had no
         * grandchild to replace it)
         */
        int fillHoleAt(int index) {
            int minGrandchildIndex;
            while ((minGrandchildIndex = findMinGrandChild(index)) > 0) {
                queue[index] = elementData(minGrandchildIndex);
                index = minGrandchildIndex;
            }
            return index;
        }

        private boolean verifyIndex(int i) {
            if ((getLeftChildIndex(i) < size) && (compareElements(i, getLeftChildIndex(i)) > 0)) {
                return false;
            }
            if ((getRightChildIndex(i) < size) && (compareElements(i, getRightChildIndex(i)) > 0)) {
                return false;
            }
            if ((i > 0) && (compareElements(i, getParentIndex(i)) > 0)) {
                return false;
            }
            if ((i > 2) && (compareElements(getGrandparentIndex(i), i) > 0)) {
                return false;
            }
            return true;
        }

        // These would be static if inner classes could have static members.

        private int getLeftChildIndex(int i) {
            return i * 2 + 1;
        }

        private int getRightChildIndex(int i) {
            return i * 2 + 2;
        }

        private int getParentIndex(int i) {
            return (i - 1) / 2;
        }

        private int getGrandparentIndex(int i) {
            return getParentIndex(getParentIndex(i)); // (i - 3) / 4
        }
    }


    @Override
    @Deprecated
    public Iterator<E> iterator() {
         throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            queue[i] = null;
        }
        size = 0;
    }

    @Override
    public Object[] toArray() {
        Object[] copyTo = new Object[size];
        System.arraycopy(queue, 0, copyTo, 0, size);
        return copyTo;
    }


}