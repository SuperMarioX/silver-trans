package com.luangeng.slivertrans.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.State.NEW;

public class TransServer extends Thread {

    private static Logger logger = LoggerFactory.getLogger(TransServer.class);

    private int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private static TransServer server = new TransServer();

    private TransServer() {
    }

    public static TransServer instance() {
        return server;
    }

    public void start(int port) {
        if (this.getState() == NEW) {
            this.port = port;
            this.bossGroup = new NioEventLoopGroup(1);
            this.workerGroup = new NioEventLoopGroup(1);
            this.start();
        } else {
            logger.error("Trans Server already started on port: " + port);
        }
    }

    @Override
    public void run() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childHandler(new TcpServerChannelInitializer());
            ChannelFuture future = bootstrap.bind(port).sync();
            logger.info("Trans Server started on port: " + port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
        }
    }

    public void shutdown() {
        if (bossGroup != null && workerGroup != null) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            logger.info("Trans Server stoped.");
        }
    }

}
