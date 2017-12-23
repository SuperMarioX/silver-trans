package com.luangeng.cmd;

import com.luangeng.CmdTool;
import com.luangeng.support.Receiver;
import com.luangeng.trans.TransClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LG on 2017/11/20.
 */
public class CmdClientHandler extends ChannelInboundHandlerAdapter {

    private static Map<Integer, String> map = new HashMap<Integer, String>();

    public static String getName(int i) {
        return map.get(Integer.valueOf(i));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        CmdTool.sendMsg(ctx.channel(), "ls");
        CmdTool.sendMsg(ctx.channel(), "pwd");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object data) throws Exception {
        String msg = (String) data;
        if (msg.startsWith("begin ")) {
            Receiver receiver = Receiver.instance();
            receiver.init(msg);
            TransClient.instance().send();
        } else if (msg.equalsIgnoreCase("end")) {
            //long t2 = System.currentTimeMillis();
            //System.out.println("[" + name + "] Over Cost: " + (t2 - t1) / 1000 + "s");
            //clear();
        } else if (msg.startsWith("ls ")) {
            praseLs(msg);
        } else if (msg.startsWith("msg ")) {
            System.out.println(msg.substring(4));
        } else {
            System.out.println(msg);
        }
        //long tt2 = System.currentTimeMillis();
        //System.err.println(count++ + " cost " + (tt2 - tt1) + "    delay " + (tt1 - lasttime));
        //lasttime = tt2;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
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
