package com.luangeng.trans;

import com.luangeng.CmdTool;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;

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
                        pipeline.addLast(new OrderDecoder(65535, 4));
                        //pipeline.addLast(new LengthFieldBasedFrameDecoder(65535,0,4,0,4));
                        pipeline.addLast(new LengthFieldPrepender(4));
                        pipeline.addLast(new TransClientHandler());
                    }
                });
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

                //ChannelFuture f
                for (int k = 0; k < 1; k++) {
                    Channel c = bootstrap.connect(ip, port).sync().channel();
                    channels.add(c);
                }

                //channels.get(0).closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                //group.shutdownGracefully();
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
            CmdTool.sendMsg(c, "hahahah");
            //c.writeAndFlush(Unpooled.buffer());
        }
    }

}
