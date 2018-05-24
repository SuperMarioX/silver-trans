package com.luangeng.slivertrans.support;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * token池
 */
public class TokenPool extends Thread {

    private static final int DEFAULT_SECOND = 600;

    private static Map<String, Long> tokenMap = new ConcurrentHashMap<>();

    static {
        new TokenPool().start();
    }

    private TokenPool() {
    }

    public static String make() {
        return make(DEFAULT_SECOND);
    }

    public static String make(int second) {
        String val = UUID.randomUUID().toString();
        long time = System.currentTimeMillis();
        tokenMap.put(val, time + second * 1000);
        return val;
    }

    public static boolean contain(String val) {
        if (val == null) {
            return false;
        }
        if (tokenMap.containsKey(val)) {
            renew(val);
            return true;
        }
        return false;
    }

    /**
     * 续约
     */
    public static void renew(String val) {
        long time = System.currentTimeMillis();
        tokenMap.put(val, time + DEFAULT_SECOND / 2 * 1000);
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        Set<String> keys = tokenMap.keySet();
        for (String key : keys) {
            if (time - tokenMap.get(key) > 0) {
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
