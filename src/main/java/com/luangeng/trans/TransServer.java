package com.luangeng.trans;

import com.luangeng.ConfigTool;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

/**
 * Created by LG on 2017/9/30.
 */
public class TransServer extends Thread {

    private int port;

    public TransServer(int port) {
        this.port = port;
    }

    public void run() {
        int PORT = Integer.valueOf(ConfigTool.getValue("server.port"));

        final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new FixedLengthFrameDecoder(40960));
                    pipeline.addLast(new TransServerHandler());
                }
            });

            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(PORT).sync();
            System.out.println("Server statrted.");

            // 等待监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
        }
    }

}
