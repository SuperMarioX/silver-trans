package com.luangeng.support;

import io.netty.buffer.ByteBuf;

public class OrderData {

    private int index;

    private ByteBuf bf;

    public OrderData(int index, ByteBuf bf) {
        this.index = index;
        this.bf = bf;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ByteBuf getBf() {
        return bf;
    }

    public void setBf(ByteBuf bf) {
        this.bf = bf;
    }
}
