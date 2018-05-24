package com.luangeng.slivertrans.http.handler;

import com.luangeng.slivertrans.support.TokenPool;
import com.luangeng.slivertrans.tools.ConfigTool;
import com.luangeng.slivertrans.tools.EncryptTool;
import com.luangeng.slivertrans.tools.HttpTool;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;

import java.util.HashMap;
import java.util.Map;

import static com.luangeng.slivertrans.tools.TransTool.CHARSET;

/**
 * Http前置处理器
 *
 * @author admin
 */
public class MainHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        if (!request.decoderResult().isSuccess()) {
            HttpTool.sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        if (request.method() != HttpMethod.GET && request.method() != HttpMethod.POST) {
            HttpTool.sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }

        final String uri = HttpTool.sanitizeUri(request.uri());
        if (uri == null) {
            HttpTool.sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        request.setUri(uri);

        if (uri.equals("/auth.action")) {
            if (loginOk(request)) {
                String token = TokenPool.make();
                HttpTool.sendMsg(ctx, token);
            } else {
                HttpTool.sendError(ctx, HttpResponseStatus.UNAUTHORIZED);
            }
        }

        if (uri.equals("") || uri.equals("/")) {
            HttpTool.sendRedirect(ctx, "/html/index.html");
            return;
        }

        ChannelFuture future = null;
        AbstractHttpHandler handler;
        if (HttpTool.isStatic(uri)) {
            handler = new StaticResourceHandler();
            future = handler.handle(ctx, request, uri);
        } else {
            if (!tokenOk(request)) {
                Map heads = new HashMap();
                heads.put("auth", "forbidden");
                HttpTool.sendError(ctx, HttpResponseStatus.UNAUTHORIZED, heads);
                return;
            }

            if (uri.contains("list.action")) {
                handler = new FileExploreHandler();
                future = handler.handle(ctx, request, uri);
            } else if (uri.contains("upload.action")) {
                handler = new FileUploadHandler();
                future = handler.handle(ctx, request, uri);
            } else if (uri.contains("view.action")) {
                handler = new FileViewHandler();
                future = handler.handle(ctx, request, uri);
            } else {
                HttpTool.sendError(ctx, HttpResponseStatus.NOT_FOUND);
            }
        }

        if (!HttpUtil.isKeepAlive(request) && future != null) {
            future.addListener(ChannelFutureListener.CLOSE);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            HttpTool.sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean tokenOk(FullHttpRequest request) {
        String token = request.headers().get("Token");
        return TokenPool.contain(token);
    }

    private boolean loginOk(FullHttpRequest request) {
        String pwd = request.content().toString(CHARSET);
        String pass = ConfigTool.getValue("pwd");
        long salt = System.currentTimeMillis() / 100000;
        pass = EncryptTool.md5(pass + salt);
        return pwd.equals(pass);
    }

}
