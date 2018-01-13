package com.luangeng.slivertrans.server;

import com.luangeng.slivertrans.support.TransDecode;
import com.luangeng.slivertrans.support.TransEncode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.State.NEW;

public class TransServer {

    private static Logger logger = LoggerFactory.getLogger(TransServer.class);

    private int port;
    private static TransServer server = new TransServer();
    private ServerThread t = new ServerThread();

    private TransServer() {
    }

    public static TransServer instance() {
        return server;
    }

    public void startup(int port) {
        if (t.getState() == NEW) {
            this.port = port;
            t.start();
        }
    }

    public void shutdown() {
        if (t != null) {
            t.shutdown();
            t = null;
        }
    }

    private class ServerThread extends Thread {
        private EventLoopGroup bossGroup;
        private EventLoopGroup workerGroup;

        @Override
        public void run() {
            try {
                bossGroup = new NioEventLoopGroup(1);
                workerGroup = new NioEventLoopGroup(1);
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 100)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new TransDecode());
                                ch.pipeline().addLast(new TransEncode());
                                ch.pipeline().addLast(new ServerHandler());
                            }
                        });
                ChannelFuture future = bootstrap.bind(port).sync();
                logger.info("Trans Server started, port: " + port);
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                logger.error("error: " + e.getMessage());
            }
        }

        public void shutdown() {
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
            logger.info("Trans Server stoped.");
        }
    }

}
