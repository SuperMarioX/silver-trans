package com.luangeng.cmd;

import com.luangeng.AppConfig;
import com.luangeng.CmdTool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;

/**
 * Created by LG on 2017/11/20.
 */
public class CmdServerHandler extends ChannelInboundHandlerAdapter {

    private String path = AppConfig.getValue("server.path");

    private File f = new File(path);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String cmd = (String) msg;
        if (cmd.equalsIgnoreCase("ls")) {
            CmdTool.sendMsg(ctx, "ls " + CmdTool.ls(path));
        } else if (cmd.startsWith("cd ")) {
            String dir = cmd.substring(3).trim();
            if (dir.equals("..")) {
                f = f.getParentFile();
                path = f.getCanonicalPath();
                CmdTool.sendMsg(ctx, "new path " + path);
                CmdTool.sendMsg(ctx, "ls " + CmdTool.ls(path));
            } else {
                String path1 = path + File.separator + dir;
                File f1 = new File(path1);
                if (f1.exists()) {
                    path = path1;
                    f = f1;
                    CmdTool.sendMsg(ctx, "new path " + path);
                    CmdTool.sendMsg(ctx, "ls " + CmdTool.ls(path));
                } else {
                    CmdTool.sendMsg(ctx, "error, path not found");
                }
            }
        } else if (cmd.startsWith("get ")) {
            String name = cmd.substring(4);
            //sent(ctx.channel(), name, path + File.separator + name);
        } else if (cmd.equalsIgnoreCase("pwd")) {
            CmdTool.sendMsg(ctx, "now at: " + path);
        } else {
            CmdTool.sendMsg(ctx, "unknow cmomand!");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
