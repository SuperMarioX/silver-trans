package com.luangeng.support;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
按序列取出队列中的元素，如果序列缺失则阻塞
 */
public class SortedBlockingQueue {

    final Lock lock = new ReentrantLock();
    final Condition canTake = lock.newCondition();
    private final AtomicInteger count = new AtomicInteger();
    private LinkedList<OrderData> list = new LinkedList<OrderData>();

    public void put(OrderData node) {
        lock.lock();
        boolean p = false;
        for (int i = 0; i < list.size(); i++) {
            if (node.getIndex() < list.get(i).getIndex()) {
                list.add(i, node);
                p = true;
                break;
            }
        }
        if (p == false) {
            list.add(node);
        }
        count.incrementAndGet();
        canTake.signal();
        lock.unlock();
    }

    public OrderData offer(int index) {
        lock.lock();
        try {
            while (list.isEmpty() || list.get(0).getIndex() != index) {
                canTake.await();
            }
            count.getAndDecrement();
            return list.pop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return null;
    }

    public int size() {
        return count.get();
    }
}
