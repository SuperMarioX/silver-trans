package com.luangeng.slivertrans.http.handler;

import com.luangeng.slivertrans.model.AppConst;
import com.luangeng.slivertrans.tools.HttpTool;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class FileViewHandler extends AbstractHttpHandler {
    @Override
    protected ChannelFuture handle(ChannelHandlerContext ctx, FullHttpRequest request, String uri) throws Exception {
        uri = uri.replace('/', File.separatorChar);
        String path = HttpTool.getParams(uri).get("path");
        path = path == null ? "" : path;
        File file = new File(AppConst.ROOT_PATH + path);
        if (!file.getCanonicalPath().startsWith(AppConst.ROOT_PATH)) {
            HttpTool.sendError(ctx, FORBIDDEN);
            return null;
        }

        if (!file.exists() || !file.isFile()) {
            HttpTool.sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return null;
        }

        RandomAccessFile raf = new RandomAccessFile(file, "r");
        long fileLength = raf.length();

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        HttpUtil.setContentLength(response, fileLength);
        response.headers().set(CONTENT_TYPE, "text/plain");
        ctx.write(response);
        ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength)).addListener((ChannelFutureListener) future -> raf.close());

        return ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
    }
}
