package com.luangeng.slivertrans.model;

public enum CmdEnum {

    UNKNOW,

    CD,

    PWD,

    GET,

    DELETE,

    LS;

    public static CmdEnum from(int s) {
        if (s < values().length) {
            return values()[s];
        }
        return UNKNOW;
    }

    public int value() {
        return this.ordinal();
    }
}
