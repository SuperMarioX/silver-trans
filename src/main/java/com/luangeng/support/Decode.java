package com.luangeng.support;

import com.luangeng.model.TransData;
import com.luangeng.model.TypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class Decode extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        TransData data = new TransData();
        data.setType(TypeEnum.get(in.readShort()));
        data.setIndex(in.readInt());
        data.setLength(in.readInt());
        data.setData(in.readBytes(data.getLength()));
        out.add(data);
    }
}
