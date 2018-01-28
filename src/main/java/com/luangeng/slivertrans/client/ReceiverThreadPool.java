package com.luangeng.slivertrans.client;

import com.luangeng.slivertrans.model.TransData;

import java.util.concurrent.*;

public class ReceiverThreadPool {

    FileReceiver receiver;

    private static ExecutorService executor = new ThreadPoolExecutor(10, 10,
            0L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new MyThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    public static void submit(Runnable run) {
        executor.execute(run);
    }

    public static void begin(TransData data) {

    }

    public static void receive(TransData data) {

    }

    public static void shutdown() {
        executor.shutdown();
    }


    //自定义线程工厂
    private static class MyThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("my thread");
            return t;
        }
    }

}
