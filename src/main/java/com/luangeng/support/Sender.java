package com.luangeng.support;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Sender {

    private static Sender task = new Sender();
    File f;
    FileInputStream in;
    FileChannel ch;
    ByteBuffer bf;

    private Sender() {
    }

    public static Sender instance() {
        return task;
    }

    public void init(String path) throws Exception {
        f = new File(path);
        if (f.exists() && f.isFile() && f.canRead()) {
            in = new FileInputStream(f);
            ch = in.getChannel();
            bf = ByteBuffer.allocate(40960);
            return;
        }
        throw new Exception("file can not read.");
    }

    public void send(Channel channel) throws IOException {
        bf.clear();
        if (ch.read(bf) != -1) {
            bf.flip();
            ByteBuf nbf = Unpooled.wrappedBuffer(bf);
            channel.writeAndFlush(nbf);
            nbf.clear();
        } else {
            ByteBuf buf = Unpooled.buffer();
            channel.writeAndFlush(buf);
            in.close();
            bf.clear();
        }
    }


}
