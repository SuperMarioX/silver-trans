package com.luangeng.trans;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

/**
 * Created by LG on 2017/9/30.
 */
public class TransClient extends Thread {

    private String ip;
    private int port;

    TransClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {

        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            bootstrap.group(group).channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new FixedLengthFrameDecoder(40960));
                    pipeline.addLast(new TransClientHandler());
                }
            });
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = bootstrap.connect(ip, port).sync();

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            System.out.println("Client stoped.");
        }
    }

}
