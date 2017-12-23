package com.luangeng;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Receiver {

    private static Receiver instance = new Receiver();

    private String path = System.getProperty("user.dir") + File.separator + "download";
    private String name;
    private long length = 0;
    private long index = 0;
    private FileOutputStream out;
    private FileChannel ch;

    private String msg;
    private long t1;

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
        t1 = System.currentTimeMillis();
    }

    public void receiver(ChannelHandlerContext ctx, ByteBuf data) throws IOException {
        if (data.capacity() == 0) {
            //
            clear();
            return;
        }
        ByteBuffer bf = data.nioBuffer();
        ch.write(bf);
        index += data.readableBytes();
        printProcess();
    }

    public void clear() throws IOException {
        out.close();
        out = null;
        ch = null;
        name = null;
        index = 0;
        msg = "";
        length = 0;
    }

    private void printProcess() {
        String process = index * 100 / length + "%";
        if (!process.equals(msg)) {
            System.out.println(process);
            msg = process;
        }
    }
}