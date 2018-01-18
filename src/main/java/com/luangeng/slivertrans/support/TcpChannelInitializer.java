package com.luangeng.slivertrans.support;

import com.luangeng.slivertrans.server.ServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class TcpChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new TransDecode());
        pipeline.addLast(new TransEncode());
        pipeline.addLast(new ServerHandler());
    }

}
