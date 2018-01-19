package com.luangeng.slivertrans.http;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpBaseHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        if (!request.decoderResult().isSuccess()) {
            HttpCommon.sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        if (request.method() != GET && request.method() != POST) {
            HttpCommon.sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }

        final String uri = request.uri();
        final String path = HttpCommon.sanitizeUri(uri);
        if (path == null) {
            HttpCommon.sendError(ctx, FORBIDDEN);
            return;
        }

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);

        ChannelFuture future = null;
        if (HttpCommon.isStatic(uri)) {
            HttpHandler handler = new StaticHandler();
            future = handler.handle(ctx, request, response, uri);
        } else if (uri.contains(".action")) {
            HttpHandler handler = new ListHandler();
            future = handler.handle(ctx, request, response, uri);
        }

        if (!HttpUtil.isKeepAlive(request) && future != null) {
            // Close the connection when the whole content is written out.
            future.addListener(ChannelFutureListener.CLOSE);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            HttpCommon.sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

}
