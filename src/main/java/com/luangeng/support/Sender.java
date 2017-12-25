package com.luangeng.support;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Sender {

    private static Sender task = new Sender();
    File f;
    FileInputStream in;
    FileChannel ch;
    ByteBuffer bf;
    private long t1;

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
            bf = ByteBuffer.allocate(40960);
            t1 = System.currentTimeMillis();
            System.out.println("send begin: " + name);
            return;
        }
        throw new Exception("file can not read.");
    }

    public void send(Channel channel) throws IOException {
        lock.lock();
        if (in == null) {
            return;
        }
        try {
            bf.clear();
            if (ch.read(bf) != -1) {
                while (!channel.isWritable()) {
                    TimeUnit.MILLISECONDS.sleep(10);
                }
                bf.flip();
                ByteBuf nbf = Unpooled.wrappedBuffer(Unpooled.copyInt(IndexGenerater.instance().get()).nioBuffer(), bf);
                channel.writeAndFlush(nbf);
                //nbf.release();
            } else {
                channel.writeAndFlush(Unpooled.copyInt(IndexGenerater.instance().get()));
                IndexGenerater.instance().reset();
                long cost = Math.round((System.currentTimeMillis() - t1) / 1000f);
                System.out.println("send over: " + f.getName() + "   Cost Time: " + cost + "s");
                clear();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void clear() throws IOException {
        in.close();
        bf.clear();
        f = null;
        in = null;
    }


}
