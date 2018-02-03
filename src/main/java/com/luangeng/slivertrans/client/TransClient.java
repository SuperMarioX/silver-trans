package com.luangeng.slivertrans.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransClient {

    private static Logger logger = LoggerFactory.getLogger(TransClient.class);

    private static TransClient client = new TransClient();

    private Bootstrap bootstrap;
    private EventLoopGroup group;

    private TransClient() {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group).channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new TcpClientChannelInitializer());
    }

    public static TransClient instance() {
        return client;
    }

    public Channel connect(String ip, int port) {
        Channel channel = null;
        try {
            channel = bootstrap.connect(ip, port).sync().channel();
            logger.info("Trans Client connect to " + ip + ":" + port);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        return channel;
    }

    public void shutdown() {
        if (group != null) {
            group.shutdownGracefully();
        }
        logger.info("Trans Client shutdown");
    }

}
