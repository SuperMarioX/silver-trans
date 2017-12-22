package com.luangeng;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class CmdTool {

    private static final Charset CHARSET = Charset.forName("UTF8");

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

    public static String getCmd(ByteBuf data) {

        StringBuilder sb = new StringBuilder();
        int length = Math.min(50, data.readableBytes());
        for (int i = 0; i < length; i++) {
            byte b = data.getByte(i);
            sb.append((char) b);
        }
        return sb.toString().trim();
    }

    public static void sendMsg(Channel ch, String msg) {
        ByteBuffer bf = CHARSET.encode(msg);
        ByteBuf bfn = Unpooled.copiedBuffer(bf);
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
