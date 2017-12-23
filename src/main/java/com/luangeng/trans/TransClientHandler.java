package com.luangeng.trans;

import com.luangeng.Receiver;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by LG on 2017/9/30.
 */
public class TransClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf data) throws Exception {
        Receiver.instance().receiver(ctx, data);
    }

}
