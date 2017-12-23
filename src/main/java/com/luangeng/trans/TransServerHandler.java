package com.luangeng.trans;

import com.luangeng.AppConfig;
import com.luangeng.CmdTool;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

public class TransServerHandler extends SimpleChannelInboundHandler<ByteBuf> {


    static int k=0;
    static long co = 0;

    public static void sent(Channel channel, String name, String filePath) throws Exception {
        String delays = AppConfig.getValue("server.delay");
        int delay = Integer.valueOf(delays);
        File f = new File(filePath);
        if (f.isFile() && f.canRead()) {
            CmdTool.sendMsg(channel, "begin " + f.getName() + "/:/" + f.length());
            System.out.println("sending " + f.getName());

            FileInputStream in = new FileInputStream(f);
            FileChannel ch = in.getChannel();
            ByteBuffer bf = ByteBuffer.allocate(40960);

            while (ch.read(bf) != -1) {
                bf.flip();
                ByteBuf nbf = Unpooled.wrappedBuffer(bf);
                channel.writeAndFlush(nbf);
                bf.clear();
                co+= bf.capacity();
                nbf.clear();
                TimeUnit.MILLISECONDS.sleep(delay);
                System.out.println(k+++ "   ");
            }
            System.out.println(co+"byte");

            in.close();
            CmdTool.sendMsg(channel, "end");
            System.out.println("send over " + name);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf data) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}