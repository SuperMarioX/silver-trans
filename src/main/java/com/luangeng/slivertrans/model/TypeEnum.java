package com.luangeng.slivertrans.model;

public enum TypeEnum {

    UNKNOW(0),

    CMD(1),

    MSG(2),

    DATA(3),

    BEGIN(4),

    END(5);

    short value;

    TypeEnum(int value) {
        this.value = (short) value;
    }

    public static TypeEnum get(short s) {
        for (TypeEnum e : TypeEnum.values()) {
            if (e.value == s) {
                return e;
            }
        }
        return UNKNOW;
    }

    public short value() {
        return this.value;
    }

}
