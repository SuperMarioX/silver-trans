package com.luangeng.cmd;

import com.luangeng.CmdTool;
import com.luangeng.trans.TransServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by LG on 2017/11/20.
 */
public class CmdServer extends Thread {

    private int port;

    public CmdServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        new CmdServer(9000).start();
        new TransServer(9000).start();
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    //.handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, CmdTool.delimiter));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new CmdServerHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("server started");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("server shuting down");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
