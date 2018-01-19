package com.luangeng.slivertrans.http;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public abstract class HttpHandler {

    protected abstract ChannelFuture handle(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response, String uri) throws Exception;

}
