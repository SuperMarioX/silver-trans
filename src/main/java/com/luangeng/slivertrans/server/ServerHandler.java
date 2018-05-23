package com.luangeng.slivertrans.server;

import com.luangeng.slivertrans.model.AppConst;
import com.luangeng.slivertrans.model.CmdEnum;
import com.luangeng.slivertrans.model.TransData;
import com.luangeng.slivertrans.model.TypeEnum;
import com.luangeng.slivertrans.support.FileSender;
import com.luangeng.slivertrans.support.FileSenderPool;
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

    private String currentPath = AppConst.ROOT_PATH;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TransData data) throws Exception {
        handle(ctx, data);
    }

    private void handle(ChannelHandlerContext ctx, TransData data) throws Exception {
        if (data.getType() == TypeEnum.CMD) {
            CmdEnum cmd = CmdEnum.fromInt(data.getIndex());
            String param = TransTool.getMsg(data);
            switch (cmd) {
                case PWD:
                    TransTool.sendMsg(ctx.channel(), ctx.channel().localAddress().toString() + currentPath);
                    break;
                case CD:
                    cd(ctx.channel(), param);
                    break;
                case LS:
                    ls(ctx.channel());
                    break;
                case GET:
                    FileSender sender = new FileSender(currentPath, param, ctx.channel());
                    FileSenderPool.submit(sender);
                    break;
                case DELETE:
                    delete(param);
                    break;
                case UNKNOW:
                    TransTool.sendMsg(ctx.channel(), "Unknow command");
                    break;
                default:
                    TransTool.sendMsg(ctx.channel(), "Unknow command");
                    break;
            }
        }
    }

    private void ls(Channel channel) {
        int k = 1;
        StringBuilder sb = new StringBuilder();
        File file = new File(currentPath);
        File[] files = file.listFiles(ff -> ff.isDirectory());
        if (files != null) {
            for (File f : files) {
                sb.append(k++);
                sb.append(AppConst.DELIMITER);
                sb.append("目录");
                sb.append(AppConst.DELIMITER);
                sb.append(f.getName());
                sb.append("\n");
            }
        }

        files = file.listFiles(ff -> ff.isFile());
        if (files != null) {
            for (File f : files) {
                sb.append(k++);
                sb.append(AppConst.DELIMITER);
                sb.append(StringTool.formatFileSize(f.length()));
                sb.append(AppConst.DELIMITER);
                sb.append(f.getName());
                sb.append("\n");
            }
        }
        TransTool.sendMsg(channel, "ls " + sb.toString());
    }

    private void cd(Channel channel, String param) {
        if (param.equals("..")) {
            File f = new File(currentPath);
            f = f.getParentFile();
            currentPath = f.getAbsolutePath();
            TransTool.sendMsg(channel, "New currentPath " + currentPath);
            ls(channel);
        } else {
            String path1 = currentPath + File.separator + param;
            File f1 = new File(path1);
            if (f1.exists() && f1.isDirectory()) {
                currentPath = path1;
                TransTool.sendMsg(channel, "New currentPath " + currentPath);
                ls(channel);
            } else {
                TransTool.sendMsg(channel, "Error, folder not exists");
            }
        }
    }

    private void delete(String path) {
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
    }

}
