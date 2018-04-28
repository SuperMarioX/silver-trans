package com.luangeng.slivertrans.support;

import com.luangeng.slivertrans.model.TransData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static com.luangeng.slivertrans.tools.TransTool.CHARSET;

/*
    消息编码
 */
public class TransDataEncode extends MessageToByteEncoder<TransData> {

    @Override
    protected void encode(ChannelHandlerContext ctx, TransData msg, ByteBuf out) throws Exception {
        out.writeBytes(Unpooled.copiedBuffer(msg.getId(), CHARSET));
        out.writeInt(msg.getType().value());
        out.writeInt(msg.getIndex());
        out.writeInt(msg.getData().readableBytes());
        out.writeBytes(msg.getData());
    }
}
