package com.luangeng.support;

import com.luangeng.model.TransData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class Encode extends MessageToByteEncoder<TransData> {

    @Override
    protected void encode(ChannelHandlerContext ctx, TransData msg, ByteBuf out) throws Exception {
        out.writeShort(msg.getType().value())
                .writeInt(msg.getIndex())
                .writeInt(msg.getData().readableBytes())
                .writeBytes(msg.getData());
    }
}
