package com.luangeng.slivertrans.support;

import com.luangeng.slivertrans.model.TransData;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
按序列取出队列中的元素，否则阻塞
 */
public class SortedBlockingQueue {

    private final Lock lock = new ReentrantLock();
    private final Condition canTake = lock.newCondition();
    private final LinkedList<TransData> list = new LinkedList<TransData>();

    public void put(TransData node) {
        lock.lock();
        try {
            int i = list.size();
            for (Iterator<TransData> iter = list.descendingIterator(); iter.hasNext(); i--) {
                if (node.getIndex() >= iter.next().getIndex()) {
                    list.add(i, node);
                    return;
                }
            }
            list.add(node);
        } finally {
            canTake.signal();
            lock.unlock();
        }
    }

    public TransData pop(int index) {
        lock.lock();
        try {
            while (list.isEmpty() || list.peek().getIndex() != index) {
                canTake.await();
            }
            return list.pop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return null;
    }

    public int size() {
        final Lock lock = this.lock;
        lock.lock();
        try {
            return list.size();
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        list.clear();
        canTake.signal();
    }

}
