package com.luangeng.slivertrans.model;

public enum TypeEnum {

    INVALID(0),

    CMD(1),

    MSG(2),

    ACK(3),

    DATA(4),

    BEGIN(5),

    END(6);

    short value;

    TypeEnum(int value) {
        this.value = (short) value;
    }

    public static TypeEnum from(short s) {
        for (TypeEnum e : TypeEnum.values()) {
            if (e.value == s) {
                return e;
            }
        }
        return INVALID;
    }

    public short value() {
        return this.value;
    }

}
