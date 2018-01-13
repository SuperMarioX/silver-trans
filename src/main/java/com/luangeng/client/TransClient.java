package com.luangeng.client;

import com.luangeng.support.Tool;
import com.luangeng.support.TransDecode;
import com.luangeng.support.TransEncode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class TransClient {

    private static Logger logger = LoggerFactory.getLogger(TransClient.class);

    private static TransClient client = new TransClient();
    private String ip;
    private int port;
    private Channel channel = null;
    private ClientThread t = new ClientThread();

    private TransClient() {
    }

    public static TransClient instance() {
        return client;
    }

    public void startup(String ip, int port) {
        this.ip = ip;
        this.port = port;
        if (!t.isAlive()) {
            t.start();
        }
    }

    public void getFile(String path) {
        Tool.sendCmd(channel, "get " + path);
    }

    public void readCmd() {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String cmd = sc.nextLine().trim();
            if (cmd.equalsIgnoreCase("exit")) {
                channel.closeFuture();
                return;
            } else if (cmd.startsWith("get ")) {
                int i = Integer.valueOf(cmd.substring(4).trim());
                cmd = "get " + ClientHandler.getFileNameByIndex(i);
            } else if (cmd.startsWith("cd ")) {
                String p = cmd;
                p = p.substring(3).trim();
                if (!p.equals("..")) {
                    try {
                        int i = Integer.valueOf(p);
                        cmd = "cd " + ClientHandler.getFileNameByIndex(i);
                    } catch (Exception e) {
                        logger.info("Invalid input: " + p);
                        return;
                    }
                }
            }
            Tool.sendCmd(channel, cmd);
        }
    }

    public void shutdown() {
        t.shutdown();
    }

    private class ClientThread extends Thread {
        private EventLoopGroup group;

        @Override
        public void run() {
            Bootstrap bootstrap = new Bootstrap();
            group = new NioEventLoopGroup();
            try {
                bootstrap.group(group).channel(NioSocketChannel.class);
                bootstrap.handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        ch.pipeline().addLast(new TransDecode());
                        ch.pipeline().addLast(new TransEncode());
                        pipeline.addLast(new ClientHandler());
                    }
                });
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

                channel = bootstrap.connect(ip, port).sync().channel();
                logger.info("Trans Client connect to " + ip + ":" + port);
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }

        public void shutdown() {
            if (group != null) {
                group.shutdownGracefully();
            }
            logger.info("Trans Client stoped.");
        }
    }

}
