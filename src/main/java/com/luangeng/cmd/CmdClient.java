package com.luangeng.cmd;

import com.luangeng.support.CmdTool;
import com.luangeng.trans.TransClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.util.Scanner;

/**
 * Created by LG on 2017/11/20.
 */
public class CmdClient extends Thread {

    private String ip;

    private int port;

    public CmdClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public static void main(String[] args) {
        new CmdClient("127.0.0.1", 9000).start();
        TransClient.instance().start("127.0.0.1", 9001);
    }

    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    //.option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, CmdTool.delimiter));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new CmdClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(ip, port).sync();

            System.out.println("Cmd client started");
            Scanner sc = new Scanner(System.in);
            while (sc.hasNextLine()) {
                String cmd = sc.nextLine().trim();
                if (cmd.equalsIgnoreCase("exit")) {
                    return;
                } else if (cmd.startsWith("get ")) {
                    int i = Integer.valueOf(cmd.substring(4).trim());
                    cmd = "get " + CmdClientHandler.getName(i);
                } else if (cmd.startsWith("cd ")) {
                    String p = cmd.substring(3).trim();
                    if (!p.equals("..")) {
                        try {
                            int i = Integer.valueOf(p);
                            cmd = "cd " + CmdClientHandler.getName(i);
                        } catch (Exception e) {
                            System.out.println("should input a num");
                            continue;
                        }
                    }
                }
                CmdTool.sendMsg(future.channel(), cmd);
            }

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            System.out.println("cmd client shutdown");
        }
    }
}
