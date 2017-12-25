package com.luangeng.trans;

import com.luangeng.CmdTool;
import com.luangeng.support.OrderData;
import com.luangeng.support.Receiver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by LG on 2017/9/30.
 */
public class TransClientHandler extends SimpleChannelInboundHandler<OrderData> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, OrderData data) throws Exception {
        System.out.println(this.hashCode() + " client read: " + data.getIndex());
        Receiver.instance().receiver(data);
        if (data.getBf().readableBytes() == 0) {
            return;
        }
        CmdTool.sendMsg(ctx.channel(), "go on");
    }

}
