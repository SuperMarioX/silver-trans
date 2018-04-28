package com.luangeng.slivertrans.model;

import io.netty.buffer.ByteBuf;

public class TransData {

    //length 32
    private String id;

    private TypeEnum type;

    private int index;

    private int length;

    private ByteBuf data;

    public TransData() {
    }

    public TransData(TypeEnum type, ByteBuf data) {
        this.type = type;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public ByteBuf getData() {
        return data;
    }

    public void setData(ByteBuf data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TransData{" +
                "type=" + type +
                ", index=" + index +
                ", length=" + length +
                '}';
    }
}
