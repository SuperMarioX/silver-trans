package com.luangeng.slivertrans.support;

import com.luangeng.slivertrans.model.TransData;
import com.luangeng.slivertrans.model.TypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

import static com.luangeng.slivertrans.tools.TransTool.CHARSET;

/*
    消息解码
 */
public class TransDataDecode extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        TransData data = new TransData();
        data.setId(in.readBytes(32).toString(CHARSET));
        data.setType(TypeEnum.from(in.readInt()));
        data.setIndex(in.readInt());
        data.setLength(in.readInt());
        data.setData(in.readBytes(data.getLength()));
        out.add(data);
    }
}
