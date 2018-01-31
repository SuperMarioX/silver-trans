package com.luangeng.slivertrans.server;

import com.luangeng.slivertrans.support.TransDataDecode;
import com.luangeng.slivertrans.support.TransDataEncode;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class TcpServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new TransDataDecode());
        pipeline.addLast(new TransDataEncode());
        pipeline.addLast(new ServerHandler());
    }

}
