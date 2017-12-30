package com.luangeng.support;

import com.luangeng.model.TransData;
import com.luangeng.model.TypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class Decode extends ReplayingDecoder<Void> {

    /**
     * Decode the from one {@link ByteBuf} to an other. This method will be called till either the input
     * {@link ByteBuf} has nothing to read when return from this method or till nothing was read from the input
     * {@link ByteBuf}.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link ByteToMessageDecoder} belongs to
     * @param in  the {@link ByteBuf} from which to read data
     * @param out the {@link List} to which decoded messages should be added
     * @throws Exception is thrown if an error occurs
     */
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
