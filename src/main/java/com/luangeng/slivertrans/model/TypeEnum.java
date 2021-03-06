package com.luangeng.slivertrans.model;

public enum TypeEnum {

    INVALID,

    CMD,

    MSG,

    ACK,

    DATA,

    BEGIN,

    END;

    public static TypeEnum from(int s) {
        if (s < values().length) {
            return values()[s];
        }
        return INVALID;
    }

    public int value() {
        return this.ordinal();
    }

}
