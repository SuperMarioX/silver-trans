package com.luangeng.trans;

import com.luangeng.support.OrderData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class OrderDecoder extends LengthFieldBasedFrameDecoder {

    public OrderDecoder(int maxFrameLength, int lengthFieldLength) {
        super(maxFrameLength, 0, lengthFieldLength, 0, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf bf = (ByteBuf) super.decode(ctx, in);
        if (bf == null) {
            return null;
        }
        int index = bf.readInt();
        OrderData data = new OrderData(index, bf);
        return data;
    }

}
