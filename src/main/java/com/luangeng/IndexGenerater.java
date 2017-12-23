package com.luangeng;

import java.util.concurrent.atomic.AtomicLong;

public class IndexGenerater {

    private static IndexGenerater gen = new IndexGenerater();

    private static AtomicLong index = new AtomicLong(1);

    private IndexGenerater() {
    }

    public static IndexGenerater instance() {
        return gen;
    }

    public long get() {
        return index.getAndAdd(1);
    }

    public void reset() {
        index.set(1);
    }
}
