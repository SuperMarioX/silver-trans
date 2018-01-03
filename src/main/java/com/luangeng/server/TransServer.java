package com.luangeng.server;

import com.luangeng.support.TransDecode;
import com.luangeng.support.TransEncode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TransServer {

    private int port;
    private static TransServer server = new TransServer();
    private Thread t = new ServerThread();

    private TransServer() {
    }

    public static TransServer instance() {
        return server;
    }

    public void start(int port) {
        this.port = port;
        t.start();
    }

    private class ServerThread extends Thread {
        @Override
        public void run() {
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup(1);
            try {
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
                System.out.println("Trans Server started, port: " + port);
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Trans Server shuting down");
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }
    }

}
