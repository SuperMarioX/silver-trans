package com.luangeng.slivertrans.server;

import com.luangeng.slivertrans.support.Tool;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

public class Sender implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(Sender.class);

    String path;
    String name;
    File f;
    FileInputStream in;
    FileChannel ch;
    ByteBuffer bf;
    //MappedByteBuffer mb;
    int index = 0;
    Channel channel;
    private long t0;

    public Sender(String path, String name, Channel c) {
        this.path = path;
        this.name = name;
        this.channel = c;
    }

    @Override
    public void run() {
        begin(path);
        send();
    }

    public void begin(String path) {
        f = new File(path + File.separator + name);
        if (!f.exists() || !f.isFile() || !f.canRead()) {
            Tool.sendMsg(channel, "file can not read.");
        }

        try {
            in = new FileInputStream(f);
            ch = in.getChannel();
            bf = ByteBuffer.allocate(20480);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        t0 = System.currentTimeMillis();
        logger.info("send begin: " + name + "  " + Tool.size(f.length()));
        Tool.sendBegin(channel, name + "/:/" + f.length());
    }

    public void send() {
        if (in == null) {
            return;
        }
        try {
            while (ch.read(bf) != -1) {
                while (!channel.isWritable()) {
                    TimeUnit.MILLISECONDS.sleep(5);
                }
                //mb = ch.map(FileChannel.MapMode.READ_ONLY,0, 1024);
                bf.flip();
                Tool.sendData(channel, bf, index);
                index++;
                bf.clear();
            }
            Tool.sendEnd(channel, index);

            long cost = Math.round((System.currentTimeMillis() - t0) / 1000f);
            logger.info("send over: " + f.getName() + "   Cost Time: " + cost + "s");
            clear();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void clear() throws IOException {
        in.close();
        bf.clear();
        f = null;
        in = null;
        index = 0;
    }


}
