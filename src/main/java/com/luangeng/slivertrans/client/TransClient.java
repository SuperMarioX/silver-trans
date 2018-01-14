package com.luangeng.slivertrans.client;

import com.luangeng.slivertrans.support.Tool;
import com.luangeng.slivertrans.support.TransDecode;
import com.luangeng.slivertrans.support.TransEncode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransClient {

    private static Logger logger = LoggerFactory.getLogger(TransClient.class);

    private static TransClient client = new TransClient();

    private String ip;
    private int port;
    private Channel channel;
    private ClientThread t;

    private TransClient() {
    }

    public static TransClient instance() {
        return client;
    }

    public void startup(String ip, int port) {
        if (t == null) {
            t = new ClientThread();
            this.ip = ip;
            this.port = port;
            t.start();
        } else {
            logger.info("need disconnect first");
        }
    }

    public void getFile(String path) {
        Tool.sendCmd(channel, "from " + path);
    }

    public void shutdown() {
        if (t != null) {
            t.shutdown();
            t = null;
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public void cmd(String cmd) {
        if (t == null) {
            logger.info("not connected");
            return;
        }
        if (cmd.startsWith("from ")) {
            int i = Integer.valueOf(cmd.substring(4).trim());
            cmd = "from " + ClientHandler.getFileNameByIndex(i);
        } else if (cmd.startsWith("cd ")) {
            String p = cmd.substring(3).trim();
            try {
                int i = Integer.valueOf(p);
                cmd = "cd " + ClientHandler.getFileNameByIndex(i);
            } catch (Exception e) {
                //nothing
            }
        }
        Tool.sendCmd(TransClient.instance().getChannel(), cmd);
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
            } catch (Exception e) {
                logger.error("error: " + e.getMessage());
                t = null;
                channel = null;
            }
        }

        public void shutdown() {
            if (group != null) {
                group.shutdownGracefully();
            }
            channel = null;
            logger.info("Trans Client disconnect");
        }
    }

}
