package com.luangeng.trans;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LG on 2017/9/30.
 */
public class TransClient {

    private String ip;
    private int port;
    private static TransClient client = new TransClient();
    private List<Channel> channels = new ArrayList<Channel>();
    private Thread t = new Thread() {
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
                        //pipeline.addLast(new LengthFieldBasedFrameDecoder());
                        pipeline.addLast(new FixedLengthFrameDecoder(40960));
                        pipeline.addLast(new TransClientHandler());
                    }
                });
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

                //ChannelFuture f
                for (int k = 0; k < 5; k++) {
                    Channel c = bootstrap.connect(ip, port).sync().channel();
                    channels.add(c);
                }

//            f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
                System.out.println("Client stoped.");
            }
        }
    };

    private TransClient() {
    }

    public static TransClient instance() {
        return client;
    }

    public void start(String ip, int port) {
        client.ip = ip;
        client.port = port;
        t.start();
    }

    public void send() {
        for (Channel c : channels) {
            c.writeAndFlush(Unpooled.buffer());
        }
    }

}
