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

    private String path = AppConfig.getValue("server.path");

    private File f = new File(path);

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
                nbf.clear();
                TimeUnit.MILLISECONDS.sleep(delay);
            }

            in.close();
            CmdTool.sendMsg(channel, "end");
            System.out.println("send over " + name);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf data) throws Exception {
        String cmd = CmdTool.getMsg(data);

        if (cmd.equalsIgnoreCase("ls")) {
            CmdTool.sendMsg(ctx, "ls " + CmdTool.ls(path));
        } else if (cmd.startsWith("cd ")) {
            String dir = cmd.substring(3).trim();
            if (dir.equals("..")) {
                f = f.getParentFile();
                path = f.getCanonicalPath();
                CmdTool.sendMsg(ctx, "msg new path " + path);
            } else {
                String path1 = path + File.separator + dir;
                File f1 = new File(path1);
                if (f1.exists()) {
                    path = path1;
                    f = f1;
                    CmdTool.sendMsg(ctx, "msg new path " + path);
                } else {
                    CmdTool.sendMsg(ctx, "msg error, path not found");
                }
            }
        } else if (cmd.startsWith("get ")) {
            String name = cmd.substring(4);
            sent(ctx.channel(), name, path + File.separator + name);
        } else if (cmd.equalsIgnoreCase("hi")) {
            CmdTool.sendMsg(ctx, "msg hi");
            System.out.println("hi");
        } else if (cmd.equalsIgnoreCase("pwd")) {
            CmdTool.sendMsg(ctx, "msg " + path);
        } else {
            CmdTool.sendMsg(ctx, "msg wrong cmd!");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}