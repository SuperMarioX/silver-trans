package com.luangeng.client;

import com.luangeng.model.TransData;
import com.luangeng.model.TypeEnum;
import com.luangeng.support.SortedQueue;
import com.luangeng.support.Tool;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Receiver {

    private static Logger logger = LoggerFactory.getLogger(Receiver.class);

    private SortedQueue queue = new SortedQueue();
    private String dstPath = System.getProperty("user.dir") + File.separator + "received";
    private String fileName;
    private long receivedSize = 0;
    private long totalSize = 0;
    private int chunkIndex = 0;
    private FileOutputStream out;
    private FileChannel ch;
    private long t1;
    private int process = 0;

    public Receiver(TransData data) {
        init(Tool.getMsg(data));
    }

    public void init(String msg) {
        String[] ss = msg.split("/:/");
        fileName = ss[0].trim();
        totalSize = Long.valueOf(ss[1].trim());
        //new File(dstPath).mkdirs();
        File f = new File(dstPath + File.separator + fileName);
        if (f.exists()) {
            f.delete();
        }
        try {
            out = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        ch = out.getChannel();
        queue.clear();
        logger.info("receive begin: " + fileName + "  " + Tool.size(totalSize));
        new MyThread().start();
        t1 = System.currentTimeMillis();
    }

    public void receiver(TransData data) {
        queue.put(data);
    }

    public void end() throws IOException {
        long cost = Math.round((System.currentTimeMillis() - t1) / 1000f);
        logger.info("receive over: " + fileName + "   Cost Time: " + cost + "s");
        if (out != null) {
            out.close();
            out = null;
            ch = null;
        }
        fileName = null;
        chunkIndex = 0;
        totalSize = 0;
        receivedSize = 0;
        process = 0;
        queue.clear();
    }

    private void printProcess() {
        int ps = (int) (receivedSize * 100 / totalSize);
        if (ps != process) {
            this.process = ps;
            logger.info(process + "% ");
            if (this.process % 10 == 0 || process >= 100) {
                //System.out.println();
            }
        }
    }

    private class MyThread extends Thread {
        public void run() {
            try {
                while (true) {
                    TransData data = queue.offer(chunkIndex++);
                    if (data.getType() == TypeEnum.END) {
                        end();
                        break;
                    }
                    ByteBuf bfn = data.getData();
                    receivedSize += data.getLength();
                    ByteBuffer bf = bfn.nioBuffer();
                    ch.write(bf);
                    printProcess();
                    bfn.release();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
