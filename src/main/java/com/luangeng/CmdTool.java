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
        int i = 0;
        StringBuilder sb = new StringBuilder();
        File file = new File(path);
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                sb.append(i);
                sb.append("/:/");
                sb.append(f.getName());
                sb.append(" ");
                sb.append(f.length() / 1024f / 1024f);
                sb.append("MB");
                sb.append("\n");
                i++;
            }
        }
        return sb.toString();
    }

    public static String getCmd(ByteBuf data) {
        StringBuilder sb = new StringBuilder();
        int length = Math.min(100, data.readableBytes());
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
//        byte[] request = new byte[data.readableBytes()];
//        data.readBytes(request);
//        System.out.println(request);;
    }
}
