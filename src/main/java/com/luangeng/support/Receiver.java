package com.luangeng.support;

import io.netty.buffer.ByteBuf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Receiver {

    private static Receiver instance = new Receiver();

    private SortedBlockingQueue queue = new SortedBlockingQueue();

    private String path = System.getProperty("user.dir") + File.separator + "download";
    private String name;
    private long nowsize = 0;
    private long length = 0;
    private int index = 0;
    private FileOutputStream out;
    private FileChannel ch;

    private long t1;

    private String processMsg;

    private Receiver() {
    }

    public static Receiver instance() {
        return instance;
    }

    public void init(String msg) throws FileNotFoundException {
        String[] ss = msg.substring(6).trim().split("/:/");
        name = ss[0].trim();
        length = Long.valueOf(ss[1].trim());
        new File(path).mkdirs();
        File f = new File(path + File.separator + name);
        if (f.exists()) {
            f.delete();
        }
        out = new FileOutputStream(f);
        ch = out.getChannel();
        queue.clear();
        new MyThread().start();
        index = 0;
        t1 = System.currentTimeMillis();
    }

    public void clear() throws IOException {
        out.close();
        out = null;
        ch = null;
        name = null;
        index = 0;
        processMsg = "";
        length = 0;
        nowsize = 0;
    }

    private void printProcess() {
        String process = nowsize * 100 / length + "%";
        if (!process.equals(processMsg)) {
            System.out.print(process + " ");
            processMsg = process;
        }
    }

    private class MyThread extends Thread {
        public void run() {
            try {
                while (true) {
                    ByteBuf bfn = queue.offer(index++).getBf();
                    printProcess();
                    nowsize += bfn.readableBytes();
                    if (bfn.readableBytes() == 0) {
                        if (nowsize == length) {
                            long cost = Math.round(System.currentTimeMillis() - t1 / 1000f);
                            System.out.println("receive over: " + name + "   Cost Time: " + cost + "s");
                            clear();
                            return;
                        } else {
                            continue;
                        }
                    }
                    ByteBuffer bf = bfn.nioBuffer();
                    ch.write(bf);
                    bfn.release();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void receiver(OrderData data) {
        queue.put(data);
    }


}
