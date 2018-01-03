package com.luangeng.client;

import com.luangeng.model.TransData;
import com.luangeng.model.TypeEnum;
import com.luangeng.support.Tool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;

public class ClientHandler extends SimpleChannelInboundHandler<TransData> {

    private static Map<Integer, String> map = new HashMap();

    Receiver receiver;

    public static String getFileNameByIndex(int i) {
        return map.get(Integer.valueOf(i));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Tool.sendCmd(ctx.channel(), "pwd");
        Tool.sendCmd(ctx.channel(), "ls");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TransData data) throws Exception {
        TypeEnum type = data.getType();
        if (type == TypeEnum.MSG) {
            String msg = Tool.getMsg(data);
            if (msg.startsWith("ls ")) {
                praseLs(msg);
            } else if (msg.startsWith("msg ")) {
                System.out.println(msg.substring(4));
            } else {
                System.out.println(msg);
            }
        } else if (type == TypeEnum.DATA || type == TypeEnum.END) {
            receiver.receiver(data);
        } else if (type == TypeEnum.BEGIN) {
            receiver = new Receiver(data);
        } else {
            System.out.println(Tool.getMsg(data));
        }
    }

    private void praseLs(String msg) {
        map.clear();
        String ss = msg.substring(3).trim();
        String[] paths = ss.split("\n");
        for (String p : paths) {
            p = p.trim();
            String[] dd = p.split("/:/");
            if (dd.length == 3) {
                System.out.println(dd[0] + " " + dd[1] + " " + dd[2]);
                map.put(Integer.valueOf(dd[0].trim()), dd[2].trim());
            }
        }
    }

}
