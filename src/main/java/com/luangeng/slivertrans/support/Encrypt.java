package com.luangeng.slivertrans.support;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Encrypt {

    private final static String PASSWORD = "1234567";

    private static String byteToString(byte[] resultBytes) {
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < resultBytes.length; i++) {
            int val = resultBytes[i] & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    public static String md5Encrypt(String info) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
        }

        byte[] srcBytes = info.getBytes();
        md5.update(srcBytes);
        byte[] resultBytes = md5.digest();
        return byteToString(resultBytes);
    }

}
