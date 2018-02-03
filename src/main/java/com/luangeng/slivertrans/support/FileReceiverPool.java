package com.luangeng.slivertrans.support;

import com.luangeng.slivertrans.model.TransData;
import com.luangeng.slivertrans.model.TypeEnum;

import java.util.Map;
import java.util.concurrent.*;

public class FileReceiverPool {

    private static Map<Long, FileReceiver> map = new ConcurrentHashMap<>();

    private static ExecutorService executor = new ThreadPoolExecutor(10, 10,
            0L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new MyThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    public static boolean receive(TransData data) {
        TypeEnum type = data.getType();
        if (type == TypeEnum.BEGIN) {
            //pool-size
            FileReceiver receiver = new FileReceiver(data);
            map.put(data.getId(), receiver);
            executor.execute(receiver);
            return true;
        } else if (type == TypeEnum.DATA || type == TypeEnum.END) {
            long id = data.getId();
            FileReceiver receiver = map.get(id);
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
            t.setName("my thread");
            return t;
        }
    }

}
