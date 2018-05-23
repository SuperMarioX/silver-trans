package com.luangeng.slivertrans.support;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * token
 */
public class TokenPool extends Thread {

    private static Map<String, Long> tokenMap = new ConcurrentHashMap<>();

    static {
        new TokenPool().start();
    }

    private TokenPool() {
    }

    public static void add(String value) {
        add(value, 600);
    }

    public static void add(String value, int second) {
        long time = System.currentTimeMillis();
        tokenMap.put(value, time + second * 1000);
    }

    public static boolean contain(String key) {
        if (key == null) {
            return false;
        }
        return tokenMap.containsKey(key);
    }

    /**
     * 续约
     */
    public static void renew(String val) {
        add(val, 600);
    }

    @Override
    public void run() {
        long tie = System.currentTimeMillis();
        Set<String> keys = tokenMap.keySet();
        for (String key : keys) {
            if (tie - tokenMap.get(key) <= 0) {
                tokenMap.remove(key);
            }
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
