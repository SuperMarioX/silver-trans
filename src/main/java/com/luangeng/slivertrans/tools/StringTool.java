package com.luangeng.slivertrans.tools;

public class StringTool {

    public static boolean isEmpty(String value) {
        return value == null || "".equals(value);
    }

    public static long toLong(String value, long def) {
        if (isEmpty(value)) {
            return def;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return def;
        }
    }

    public static int toInt(String value, int def) {
        if (isEmpty(value)) {
            return def;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return def;
        }
    }

    public static String size(long num) {
        long m = 1 << 20;
        if (num / m == 0) {
            return (num / 1024) + "KB";
        }
        return num / m + "MB";
    }
}
