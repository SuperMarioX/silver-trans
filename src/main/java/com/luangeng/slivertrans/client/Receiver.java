package com.luangeng.slivertrans.client;

import com.luangeng.slivertrans.model.AppConst;
import com.luangeng.slivertrans.model.TransData;
import com.luangeng.slivertrans.model.TypeEnum;
import com.luangeng.slivertrans.tools.StringTool;
import com.luangeng.slivertrans.tools.TransTool;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Receiver implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(Receiver.class);

    private SortedBlockingQueue queue = new SortedBlockingQueue();
    private String dstPath = AppConst.ROOT;
    private String fileName;
    private long receivedSize = 0;
    private long totalSize;
    private FileOutputStream out;
    private FileChannel ch;
    private long t0;
    private int process = 0;

    public Receiver(TransData data) {
        String msg = TransTool.getMsg(data);
        String[] ss = msg.split(AppConst.DELIMITER);
        fileName = ss[0].trim();
        totalSize = Long.valueOf(ss[1].trim());
        File dstDir = new File(dstPath);
        if (!dstDir.exists()) {
            dstDir.mkdirs();
        }
        File dstFile = new File(dstPath + File.separator + fileName);
        if (dstFile.exists()) {

        }
        try {
            out = new FileOutputStream(dstFile);
        } catch (FileNotFoundException e) {
            logger.error("error: " + e.getMessage());
        }
        ch = out.getChannel();
        logger.info("Receiving: " + fileName + "  Size: " + StringTool.size(totalSize));
    }

    public void receiver(TransData data) {
        queue.put(data);
    }

    public void finish() throws IOException {
        long cost = Math.round((System.currentTimeMillis() - t0) / 1000f);
        logger.info("Receive complete: " + fileName + "   Cost Time: " + cost + "s");
        if (out != null) {
            out.close();
            out = null;
            ch = null;
        }
        fileName = null;
        totalSize = 0;
        receivedSize = 0;
        process = 0;
    }

    private void printProcess() {
        int ps = (int) (receivedSize * 100 / totalSize);
        if (ps != process) {
            this.process = ps;
            logger.info(process + "% ");
        }
    }

    @Override
    public void run() {
        t0 = System.currentTimeMillis();
        int chunkIndex = 0;
        try {
            while (true) {
                TransData data = queue.pop(chunkIndex++);
                if (data.getType() == TypeEnum.END) {
                    finish();
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
