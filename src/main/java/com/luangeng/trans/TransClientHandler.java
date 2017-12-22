package com.luangeng.trans;

import com.luangeng.CmdTool;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LG on 2017/9/30.
 */
public class TransClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static Map<Integer, String> map = new HashMap<Integer, String>();
    private String path = System.getProperty("user.dir") + File.separator + "download";
    private String name;
    private long length = 0;
    private long now = 0;
    private FileOutputStream out;
    private FileChannel ch;
    private String msg;
    private long t1;

    int count = 0;
    long lasttime = 0;

    public static String getName(int i) {
        return map.get(Integer.valueOf(i));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        CmdTool.sendMsg(ctx.channel(), "hi");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf data) throws Exception {

//    	System.out.println("capacity"+data.capacity());
//    	System.out.println("readableBytes"+data.readableBytes());
//    	System.out.println("maxCapacity"+data.maxCapacity());

        long tt1 = System.currentTimeMillis();
        System.err.println("from last " + (tt1 - lasttime));

        String cmd = CmdTool.getCmd(data);

        if (cmd.startsWith("begin ")) {
            String[] ss = CmdTool.getMsg(data).substring(6).trim().split("/:/");
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
        } else if (cmd.equalsIgnoreCase("end")) {
            long t2 = System.currentTimeMillis();
            System.out.println("[" + name + "] Over Cost: " + (t2 - t1) / 1000 + "s");
            clear();
        } else if (cmd.startsWith("ls ")) {
            praseLs(data);
        } else if (cmd.startsWith("msg ")) {
            System.out.println(cmd.substring(4));
        } else {
            ByteBuffer bf = data.nioBuffer();
            ch.write(bf);
            now += data.readableBytes();
            printProcess();
        }
        long tt2 = System.currentTimeMillis();
        System.out.println("Cost " + count++ + " of " + (tt2 - tt1));

        lasttime = tt2;
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

    private void praseLs(ByteBuf data) {
        map.clear();
        String ss = CmdTool.getMsg(data).substring(3).trim();
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
