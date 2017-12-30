package com.luangeng.server;

import com.luangeng.model.TransData;
import com.luangeng.model.TypeEnum;
import com.luangeng.support.Tool;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;

public class ServerHandler extends SimpleChannelInboundHandler<TransData> {

    private String cpath = System.getProperty("user.dir");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TransData data) throws Exception {
        handle(ctx, data);
    }

    private void handle(ChannelHandlerContext ctx, TransData data) throws Exception {
        if (data.getType() == TypeEnum.CMD) {
            String cmd = Tool.getMsg(data);
            if (cmd.equalsIgnoreCase("ls")) {
                ls(ctx.channel());
            } else if (cmd.startsWith("cd ")) {
                cd(ctx.channel(), cmd);
            } else if (cmd.startsWith("get ")) {
                String name = cmd.substring(4);
                Sender sender = new Sender(cpath, name, ctx.channel());
                SenderThreadPool.exe(sender);
            } else if (cmd.equalsIgnoreCase("pwd")) {
                Tool.sendMsg(ctx.channel(), "now at: " + cpath);
            } else {
                Tool.sendMsg(ctx.channel(), "unknow command!");
            }
        }
    }

    private void ls(Channel channel) {
        int k = 0;
        StringBuilder sb = new StringBuilder();
        File file = new File(cpath);
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
                sb.append(Tool.size(f.length()));
                sb.append("/:/");
                sb.append(f.getName());
                sb.append("\n");
                k++;
            }
        }
        Tool.sendMsg(channel, "ls " + sb.toString());
    }

    private void cd(Channel channel, String cmd) {
        String dir = cmd.substring(3).trim();
        if (dir.equals("..")) {
            File f = new File(cpath);
            f = f.getParentFile();
            cpath = f.getAbsolutePath();
            Tool.sendMsg(channel, "new path " + cpath);
            ls(channel);
        } else {
            String path1 = cpath + File.separator + dir;
            File f1 = new File(path1);
            if (f1.exists()) {
                cpath = path1;
                Tool.sendMsg(channel, "new path " + cpath);
                ls(channel);
            } else {
                Tool.sendMsg(channel, "error, path not found");
            }
        }
    }


}
