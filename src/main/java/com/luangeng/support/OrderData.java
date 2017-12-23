package com.luangeng.support;

import io.netty.buffer.ByteBuf;

public class OrderData {

    private long index;

    private ByteBuf bf;

    public OrderData(long index, ByteBuf bf) {
        this.index = index;
        this.bf = bf;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public ByteBuf getBf() {
        return bf;
    }

    public void setBf(ByteBuf bf) {
        this.bf = bf;
    }
}
