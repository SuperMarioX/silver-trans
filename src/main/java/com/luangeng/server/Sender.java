package com.luangeng.server;

import com.luangeng.support.Tool;
import io.netty.channel.Channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

public class Sender implements Runnable {

    String path;
    String name;
    File f;
    FileInputStream in;
    FileChannel ch;
    ByteBuffer bf;
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
            bf = ByteBuffer.allocate(40960);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        t0 = System.currentTimeMillis();
        System.out.println("send begin: " + name + "  " + Tool.size(f.length()));
        Tool.sendBegin(channel, name + "/:/" + f.length());
    }

    public void send() {
        if (in == null) {
            return;
        }
        try {
            while (ch.read(bf) != -1) {
                //while (!channel.isWritable()) {
                TimeUnit.MILLISECONDS.sleep(2);
                //}
                bf.flip();
                Tool.sendData(channel, bf, index);
                index++;
                bf.clear();
                //nbf.release();
            }
            Tool.sendEnd(channel, index);

            long cost = Math.round((System.currentTimeMillis() - t0) / 1000f);
            System.out.println("send over: " + f.getName() + "   Cost Time: " + cost + "s");
            clear();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
