package com.luangeng.trans;

import com.luangeng.support.IndexGenerater;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class OrderEncoder extends MessageToMessageEncoder<ByteBuf> {

    private IndexGenerater gen;

    public OrderEncoder(IndexGenerater gen) {
        this.gen = gen;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
//        out.add(ctx.alloc().buffer(4).writeInt(gen.get()));
        // TODO
        byteBuf = Unpooled.copiedBuffer(ctx.alloc().buffer(4).writeLong(gen.get()), byteBuf);
        out.add(byteBuf.retain());
    }
}
