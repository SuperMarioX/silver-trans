package com.luangeng.slivertrans.http;

import com.luangeng.slivertrans.model.AppConst;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;

public class StaticHandler extends HttpHandler {

    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

    public ChannelFuture handle(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response, String path) throws Exception {

        File file = new File(AppConst.BASE_DIR + path);
        if (!file.exists() || file.isHidden()) {
            HttpCommon.sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return null;
        }
        if (!file.isFile()) {
            HttpCommon.sendError(ctx, FORBIDDEN);
            return null;
        }

        // Cache Validation
        if (isModified(request, file) == false) {
            HttpCommon.sendNotModified(ctx);
            return null;
        }

        FileChannel channel = new RandomAccessFile(file, "r").getChannel();
        long fileLength = channel.size();
        HttpUtil.setContentLength(response, fileLength);
        HttpCommon.setContentTypeHeader(response, file);
        HttpCommon.setDateAndCacheHeaders(response, file);

        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (channel.read(buffer) != -1) {
            buffer.flip();
            response.content().writeBytes(buffer);
            buffer.clear();
        }
        channel.close();

        return ctx.writeAndFlush(response);
    }

    private boolean isModified(FullHttpRequest request, File file) {
        String ifModifiedSince = request.headers().get(HttpHeaderNames.IF_MODIFIED_SINCE);
        if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
            Date ifModifiedSinceDate = null;
            try {
                ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);
            } catch (ParseException e) {
                e.printStackTrace();
                return true;
            }
            // Only compare up to the second because the datetime format we send to the client
            // does not have milliseconds
            long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
            long fileLastModifiedSeconds = file.lastModified() / 1000;
            if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
                return false;
            }
        }
        return true;
    }

}
