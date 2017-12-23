package com.luangeng.cmd;

import com.luangeng.CmdTool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LG on 2017/11/20.
 */
public class CmdClientHandler extends ChannelInboundHandlerAdapter {

    private static Map<Integer, String> map = new HashMap<Integer, String>();
    int count = 0;
    long lasttime = 0;
    private String path = System.getProperty("user.dir") + File.separator + "download";
    private String name;
    private long length = 0;
    private long now = 0;
    private FileOutputStream out;
    private FileChannel ch;
    private String msg;
    private long t1;

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

//    	System.out.println("capacity"+data.capacity());
//    	System.out.println("readableBytes"+data.readableBytes());
//    	System.out.println("maxCapacity"+data.maxCapacity());

        long tt1 = System.currentTimeMillis();

        String msg = (String) data;

        if (msg.startsWith("begin ")) {
            String[] ss = msg.substring(6).trim().split("/:/");
            name = ss[0].trim();
            length = Long.valueOf(ss[1].trim());
            new File(path).mkdirs();
            File f = new File(path + File.separator + name);
            if (f.exists()) {
                f.delete();
            }
            out = new FileOutputStream(f);
            ch = out.getChannel();
            t1 = System.currentTimeMillis();
        } else if (msg.equalsIgnoreCase("end")) {
            long t2 = System.currentTimeMillis();
            System.out.println("[" + name + "] Over Cost: " + (t2 - t1) / 1000 + "s");
            clear();
        } else if (msg.startsWith("ls ")) {
            praseLs(msg);
        } else if (msg.startsWith("msg ")) {
            System.out.println(msg.substring(4));
        } else {
//            ByteBuffer bf = msg.nioBuffer();
//            ch.write(bf);
//            now += msg.readableBytes();
//            printProcess();
            System.out.println(msg);
        }
        long tt2 = System.currentTimeMillis();
        //System.err.println(count++ + " cost " + (tt2 - tt1) + "    delay " + (tt1 - lasttime));
        lasttime = tt2;
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

    private void clear() throws IOException {
        out.close();
        out = null;
        ch = null;
        name = null;
        now = 0;
        msg = "";
        length = 0;
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

    private void printProcess() {
        String process = now * 100 / length + "%";
        if (!process.equals(msg)) {
            System.out.println(process);
            msg = process;
        }
    }
}
