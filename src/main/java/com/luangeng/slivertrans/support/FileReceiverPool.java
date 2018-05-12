package com.luangeng.slivertrans.support;

import com.luangeng.slivertrans.client.ClientHandler;
import com.luangeng.slivertrans.model.TransData;
import com.luangeng.slivertrans.model.TypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/*
    文件接收线程池
 */
public class FileReceiverPool {

    private static final int DEFAULT_POOL_SIZE = 5;

    private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private static Map<String, FileReceiver> mapId2Receiver = new ConcurrentHashMap<>();

    private static ExecutorService executor = new ThreadPoolExecutor(DEFAULT_POOL_SIZE, DEFAULT_POOL_SIZE,
            0L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new MyThreadFactory(), new MyCallerRunsPolicy());

    public static boolean receive(TransData data) {
        TypeEnum type = data.getType();
        if (type == TypeEnum.BEGIN) {
            FileReceiver receiver = new FileReceiver(data);
            mapId2Receiver.put(data.getId(), receiver);
            executor.execute(receiver);
            return true;
        } else if (type == TypeEnum.DATA || type == TypeEnum.END) {
            String id = data.getId();
            FileReceiver receiver = mapId2Receiver.get(id);
            if (receiver == null) {
                return false;
            }
            receiver.receiver(data);
            return true;
        }
        return false;
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
