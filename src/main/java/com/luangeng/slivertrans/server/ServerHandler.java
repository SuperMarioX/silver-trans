package com.luangeng.slivertrans.server;

import com.luangeng.slivertrans.model.AppConst;
import com.luangeng.slivertrans.model.TransData;
import com.luangeng.slivertrans.model.TypeEnum;
import com.luangeng.slivertrans.tools.StringTool;
import com.luangeng.slivertrans.tools.TransTool;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ServerHandler extends SimpleChannelInboundHandler<TransData> {

    private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    private String cpath = System.getProperty("user.dir");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TransData data) throws Exception {
        handle(ctx, data);
    }

    private void handle(ChannelHandlerContext ctx, TransData data) throws Exception {
        if (data.getType() == TypeEnum.CMD) {
            String cmd = TransTool.getMsg(data);
            if (cmd.equalsIgnoreCase("ls")) {
                ls(ctx.channel());
            } else if (cmd.startsWith("cd ")) {
                cd(ctx.channel(), cmd);
            } else if (cmd.startsWith("get ")) {
                String name = cmd.substring(4).trim();
                FileSender sender = new FileSender(cpath, name, ctx.channel());
                SenderThreadPool.submit(sender);
            } else if (cmd.equalsIgnoreCase("pwd")) {
                TransTool.sendMsg(ctx.channel(), ctx.channel().localAddress().toString() + cpath);
            } else {
                TransTool.sendMsg(ctx.channel(), "Unknow command");
            }
        }
    }

    private void ls(Channel channel) {
        int k = 0;
        StringBuilder sb = new StringBuilder();
        File file = new File(cpath);
        File[] files = file.listFiles(ff -> ff.isDirectory());
        if (files != null)
            for (File f : files) {
                sb.append(k);
                sb.append(AppConst.DELIMITER);
                sb.append("目录");
                sb.append(AppConst.DELIMITER);
                sb.append(f.getName());
                sb.append("\n");
                k++;
            }

        files = file.listFiles(ff -> ff.isFile());
        if (files != null)
            for (File f : files) {
                sb.append(k);
                sb.append(AppConst.DELIMITER);
                sb.append(StringTool.size(f.length()));
                sb.append(AppConst.DELIMITER);
                sb.append(f.getName());
                sb.append("\n");
                k++;
            }
        TransTool.sendMsg(channel, "ls " + sb.toString());
    }

    private void cd(Channel channel, String cmd) {
        String dir = cmd.substring(3).trim();
        if (dir.equals("..")) {
            File f = new File(cpath);
            f = f.getParentFile();
            cpath = f.getAbsolutePath();
            TransTool.sendMsg(channel, "New path " + cpath);
            ls(channel);
        } else {
            String path1 = cpath + File.separator + dir;
            File f1 = new File(path1);
            if (f1.exists() && f1.isDirectory()) {
                cpath = path1;
                TransTool.sendMsg(channel, "New path " + cpath);
                ls(channel);
            } else {
                TransTool.sendMsg(channel, "Error, folder not found");
            }
        }
    }

}
