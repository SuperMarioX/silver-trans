package com.luangeng.slivertrans.support;

import com.luangeng.slivertrans.model.TransData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TransDataEncode extends MessageToByteEncoder<TransData> {

    @Override
    protected void encode(ChannelHandlerContext ctx, TransData msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getType().value())
                .writeInt(msg.getIndex())
                .writeInt(msg.getData().readableBytes())
                .writeBytes(msg.getData());
    }
}