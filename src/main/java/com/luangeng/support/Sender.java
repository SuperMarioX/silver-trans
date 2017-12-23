package com.luangeng.support;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.locks.ReentrantLock;

public class Sender {

    private static Sender task = new Sender();
    File f;
    FileInputStream in;
    FileChannel ch;
    ByteBuffer bf;

    private static int i = 0;
    private ReentrantLock lock = new ReentrantLock();

    private Sender() {
    }

    public static Sender instance() {
        return task;
    }

    public void init(String path, String name) throws Exception {
        f = new File(path + File.separator + name);
        if (f.exists() && f.isFile() && f.canRead()) {
            in = new FileInputStream(f);
            ch = in.getChannel();
            bf = ByteBuffer.allocate(4096);
            return;
        }
        throw new Exception("file can not read.");
    }

    public void send(Channel channel) throws IOException {
        lock.lock();
        bf.clear();
        if (ch.read(bf) != -1) {
            bf.flip();
            ByteBuf nbf = Unpooled.wrappedBuffer(bf);
            channel.writeAndFlush(nbf);
            nbf.clear();
            i++;
            System.out.println("send:" + i);
        }
        lock.unlock();
    }


}
