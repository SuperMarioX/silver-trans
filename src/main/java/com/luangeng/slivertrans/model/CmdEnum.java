package com.luangeng.slivertrans.model;

public enum CmdEnum {

    UNKNOW,

    CD,

    PWD,

    GET,

    DELETE,

    LS;

    public static CmdEnum fromInt(int s) {
        if (s < values().length) {
            return values()[s];
        }
        return UNKNOW;
    }

    public static CmdEnum fromStr(String str) {
        for (CmdEnum e : values()) {
            if (e.name().equalsIgnoreCase(str)) {
                return e;
            }
        }
        return UNKNOW;
    }

    public int value() {
        return this.ordinal();
    }
}
