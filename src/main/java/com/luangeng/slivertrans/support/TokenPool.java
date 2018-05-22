package com.luangeng.slivertrans.support;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/*
 *
 *
 * */
public class TokenPool extends Thread {

    private static Map<String, Integer> tokenMap = new ConcurrentHashMap<>();

    static {
        new TokenPool().start();
    }

    private TokenPool() {
    }

    public static void add(String value, int second) {
        tokenMap.put(value, second);
    }

    public static boolean contain(String key) {
        if (key == null) {
            return false;
        }
        return tokenMap.containsKey(key);
    }

    public void run() {
        Set<String> keys = tokenMap.keySet();
        for (String key : keys) {
            if (tokenMap.get(key) == 1) {
                tokenMap.remove(key);
            }
            tokenMap.put(key, tokenMap.get(key) - 1);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
