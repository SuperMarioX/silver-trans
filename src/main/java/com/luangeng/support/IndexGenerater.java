package com.luangeng.support;

import java.util.concurrent.atomic.AtomicInteger;

public class IndexGenerater {

    private static IndexGenerater gen = new IndexGenerater();

    private static AtomicInteger index = new AtomicInteger(0);

    private IndexGenerater() {
    }

    public static IndexGenerater instance() {
        return gen;
    }

    public int get() {
        return index.getAndAdd(1);
    }

    public void reset() {
        index.set(1);
    }
}
