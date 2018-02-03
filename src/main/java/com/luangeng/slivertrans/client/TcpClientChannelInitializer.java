package com.luangeng.slivertrans.client;

import com.luangeng.slivertrans.support.TransDataDecode;
import com.luangeng.slivertrans.support.TransDataEncode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

public class TcpClientChannelInitializer extends ChannelInitializer {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new TransDataDecode());
        pipeline.addLast(new TransDataEncode());
        pipeline.addLast(new ClientHandler());
    }

}
