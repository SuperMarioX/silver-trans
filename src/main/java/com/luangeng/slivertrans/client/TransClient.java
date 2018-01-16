package com.luangeng.slivertrans.client;

import com.luangeng.slivertrans.support.TransDecode;
import com.luangeng.slivertrans.support.TransEncode;
import com.luangeng.slivertrans.support.TransTool;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.State.NEW;

public class TransClient extends Thread {

    private static Logger logger = LoggerFactory.getLogger(TransClient.class);

    private String ip;
    private int port;
    private Channel channel;
    private EventLoopGroup group;

    public void start(String ip, int port) {
        if (this.getState() == NEW) {
            this.ip = ip;
            this.port = port;
            this.start();
        } else {
            logger.info("Trans Client already connect with " + this.getAddress());
        }
    }

    public void sendCmd(String cmd) {
        if (channel == null) {
            logger.info("not connected");
            return;
        }
        if (cmd.startsWith("get ")) {
            int i = Integer.valueOf(cmd.substring(4).trim());
            cmd = "get " + ClientHandler.getFileNameByIndex(i);
        } else if (cmd.startsWith("cd ")) {
            String p = cmd.substring(3).trim();
            try {
                int i = Integer.valueOf(p);
                cmd = "cd " + ClientHandler.getFileNameByIndex(i);
            } catch (Exception e) {
                //nothing
            }
        }
        TransTool.sendCmd(channel, cmd);
    }


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
            logger.info("Trans Client connect with " + ip + ":" + port);
            channel.closeFuture().sync();
        } catch (Exception e) {
            logger.error("error: " + e.getMessage());
            channel = null;
        }
    }

    public void shutdown() {
        if (group != null) {
            group.shutdownGracefully();
        }
        channel = null;
        logger.info("Trans Client disconnect from " + this.getAddress());
    }

    public void getFile(String path) {
        TransTool.sendCmd(channel, "get " + path);
    }

    public Channel getChannel() {
        return channel;
    }

    public String getAddress() {
        return this.ip + ":" + this.port;
    }
}
