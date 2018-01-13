package com.luangeng.slivertrans.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SenderThreadPool {

    private static ExecutorService exe = Executors.newFixedThreadPool(10);

    public static void exe(Runnable run) {
        exe.execute(run);
    }

}
