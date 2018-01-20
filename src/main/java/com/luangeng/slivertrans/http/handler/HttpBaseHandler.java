package com.luangeng.slivertrans.http.handler;

import com.luangeng.slivertrans.tools.HttpTool;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;

public class HttpBaseHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        if (!request.decoderResult().isSuccess()) {
            HttpTool.sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        if (request.method() != GET && request.method() != POST) {
            HttpTool.sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }

        final String uri = request.uri();
        final String path = HttpTool.sanitizeUri(uri);
        if (path == null) {
            HttpTool.sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }

        ChannelFuture future = null;
        AbstractHttpHandler handler;
        if (HttpTool.isStatic(uri)) {
            handler = new StaticResourceHandler();
            future = handler.handle(ctx, request, uri);
        } else if (uri.contains("list.action")) {
            handler = new FileExploreHandler();
            future = handler.handle(ctx, request, uri);
        } else if (uri.contains("upload.action")) {
            handler = new FileUploadHandler();
            handler.handle(ctx, request, uri);
        } else {
            HttpTool.sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }

        if (!HttpUtil.isKeepAlive(request) && future != null) {
            future.addListener(ChannelFutureListener.CLOSE);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            HttpTool.sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

}
