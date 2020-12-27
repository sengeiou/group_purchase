/*
 * Copyright Ningbo Qishan Technology Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mds.group.purchase.utils;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The type Limit queue.
 *
 * @param <E> the type parameter
 * @author pavawi *
 */
public class LimitQueue<E> implements Queue<E>, Serializable {

    /**
     * 队列长度，实例化类的时候指定
     */
    private int limit;

    private Queue<E> queue = new LinkedList<E>();

    /**
     * Instantiates a new Limit queue.
     *
     * @param limit the limit
     */
    public LimitQueue(int limit) {
        this.limit = limit;
    }

    /**
     * Instantiates a new Limit queue.
     */
    public LimitQueue() {
        this.limit = 50;
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return queue.contains(o);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return queue.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return queue.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return queue.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return queue.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return queue.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        return queue.addAll(c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return queue.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return queue.retainAll(c);
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public boolean equals(Object o) {
        return queue.equals(o);
    }

    @Override
    public int hashCode() {
        return queue.hashCode();
    }

    /**
     * 入队
     * @param e
     * @return
     */
    @Override
    public boolean offer(E e) {
        if (queue.size() >= limit) {
            //如果超出长度，入队时先出队
            queue.poll();
        }
        return queue.offer(e);
    }

    @Override
    public E remove() {
        return queue.remove();
    }

    /**
     * 出队
     * @return
     */
    @Override
    public E poll() {
        return queue.poll();
    }

    @Override
    public E element() {
        return queue.element();
    }

    @Override
    public E peek() {
        return queue.peek();
    }

    /**
     * 获取队列
     *
     * @return queue
     */
    public Queue<E> getQueue() {
        return queue;
    }

    /**
     * Get limit int.
     *
     * @return the int
     */
    public int getLimit() {
        return limit;
    }
}
