package com.luangeng.slivertrans.http.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public abstract class AbstractHttpHandler {

    protected abstract ChannelFuture handle(ChannelHandlerContext ctx, FullHttpRequest request, String uri) throws Exception;

}
