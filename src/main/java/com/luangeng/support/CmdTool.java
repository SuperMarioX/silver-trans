package com.luangeng.support;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class CmdTool {

    public static final Charset CHARSET = Charset.forName("UTF8");

    public static final String delimiterString = "§";

    public static final ByteBuf delimiter = Unpooled.copiedBuffer("§".getBytes());

    public static String ls(String path) {
        int k = 0;
        StringBuilder sb = new StringBuilder();
        File file = new File(path);
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                sb.append(k);
                sb.append("/:/");
                sb.append("目录");
                sb.append("/:/");
                sb.append(f.getName());
                sb.append("\n");
                k++;
            }
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                sb.append(k);
                sb.append("/:/");
                sb.append(size(f.length()));
                sb.append("/:/");
                sb.append(f.getName());
                sb.append("\n");
                k++;
            }
        }
        return sb.toString();
    }

    public static void sendMsg(Channel ch, String msg) {
        ByteBuffer bf = CHARSET.encode(msg + delimiterString);
        ByteBuf bfn = Unpooled.wrappedBuffer(bf);
        ch.writeAndFlush(bfn);
    }

    public static void sendMsg(ChannelHandlerContext ctx, String msg) {
        sendMsg(ctx.channel(), msg);
    }

    public static String getMsg(ByteBuf bf) {
        CharBuffer cb = CHARSET.decode(bf.nioBuffer());
        return cb.toString().trim();
    }

    private static String size(long num) {
        long m = 1 << 20;
        if (num / m == 0) {
            return (num / 1024) + "KB";
        }
        return num / m + "MB";
    }
}
