package com.luangeng.slivertrans.support;

import com.luangeng.slivertrans.client.ClientHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/*
    文件发送线程池
 */
public class FileSenderPool {

    private static final int DEFAULT_POOL_SIZE = 5;

    private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private static ExecutorService executor = new ThreadPoolExecutor(DEFAULT_POOL_SIZE, DEFAULT_POOL_SIZE,
            0L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new MyThreadFactory(), new MyCallerRunsPolicy());


    public static void submit(Runnable run) {
        executor.execute(run);
    }


    public static void shutdown() {
        executor.shutdown();
    }


    //自定义线程工厂
    private static class MyThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
            return t;
        }
    }

    // 自定义线程异常处理
    private static class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            //log
            logger.error(t.getName(), e);
        }
    }

    // 自定义线程池溢出处理
    private static class MyCallerRunsPolicy extends ThreadPoolExecutor.CallerRunsPolicy {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            logger.error("File Sender Pool full!");
            super.rejectedExecution(r, e);
        }
    }

}
