package com.luangeng.slivertrans.tools;

import java.text.SimpleDateFormat;

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

    public static String size(long num) {
        long m = 1 << 20;
        if (num / m == 0) {
            return (num / 1024) + "KB";
        }
        return num / m + "MB";
    }

    public static String date(long date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
}
