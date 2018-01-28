package com.luangeng.slivertrans.client;

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

    private Bootstrap bootstrap;
    private EventLoopGroup group;

    private TransClient() {
        init();
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

    public void init() {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group).channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new TransDecode());
                pipeline.addLast(new TransEncode());
                pipeline.addLast(new ClientHandler());
            }
        });
    }

    public void shutdown() {
        if (group != null) {
            group.shutdownGracefully();
        }
        logger.info("Trans Client shutdown");
    }

}
