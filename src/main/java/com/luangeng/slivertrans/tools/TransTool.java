package com.luangeng.slivertrans.tools;

import com.luangeng.slivertrans.model.CmdEnum;
import com.luangeng.slivertrans.model.TransData;
import com.luangeng.slivertrans.model.TypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Objects;

public class TransTool {

    public static final Charset CHARSET = CharsetUtil.UTF_8;

    public static void sendMsg(Channel ch, String msg) {
        ByteBuf bfn = Unpooled.copiedBuffer(msg, CHARSET);
        TransData data = new TransData(TypeEnum.MSG, bfn);
        ch.writeAndFlush(data);
    }

    public static void sendCmd(Channel ch, CmdEnum cmd, String param) {
        Objects.requireNonNull(ch);
        ByteBuf bfn = Unpooled.copiedBuffer(param, CHARSET);
        TransData data = new TransData(TypeEnum.CMD, bfn);
        data.setIndex(cmd.value());
        ch.writeAndFlush(data);
    }

    public static String getMsg(TransData data) {
        if (data.getType() != TypeEnum.DATA) {
            return data.getData().toString(CHARSET);
        }
        return null;
    }


    public static void sendBegin(Channel ch, String msg) {
        ByteBuf bfn = Unpooled.copiedBuffer(msg, CHARSET);
        TransData data = new TransData(TypeEnum.BEGIN, bfn);
        data.setIndex(0);
        ch.writeAndFlush(data);
    }

    public static void sendData(Channel ch, ByteBuffer bf, int index) {
        TransData data = new TransData();
        data.setType(TypeEnum.DATA);
        ByteBuf bfn = Unpooled.copiedBuffer(bf);
        data.setData(bfn);
        data.setIndex(index);
        ch.writeAndFlush(data);
    }

    public static void sendEnd(Channel ch, int index) {
        TransData data = new TransData(TypeEnum.END, Unpooled.EMPTY_BUFFER);
        data.setIndex(index);
        ch.writeAndFlush(data);
    }
}
