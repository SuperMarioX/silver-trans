package com.luangeng.slivertrans.http.handler;

import com.luangeng.slivertrans.model.AppConst;
import com.luangeng.slivertrans.tools.HttpTool;
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
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class StaticResourceHandler extends AbstractHttpHandler {

    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

    public ChannelFuture handle(ChannelHandlerContext ctx, FullHttpRequest request, String uri) throws Exception {
        uri = uri.replace('/', File.separatorChar);
        File file = new File(AppConst.ASSETS_DIR + uri);
        if (!file.getCanonicalPath().startsWith(AppConst.ASSETS_DIR)) {
            HttpTool.sendError(ctx, FORBIDDEN);
            return null;
        }
        if (!AppConst.ALLOWED_FILE_NAME.matcher(file.getName()).matches()) {
            HttpTool.sendError(ctx, FORBIDDEN);
            return null;
        }

        if (!file.exists() || file.isHidden() || !file.isFile()) {
            HttpTool.sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return null;
        }

        // Cache Validation
        if (isModified(request, file) == false) {
            HttpTool.sendNotModified(ctx);
            return null;
        }

        FileChannel fileChannel = new RandomAccessFile(file, "r").getChannel();
        long fileLength = fileChannel.size();

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        HttpUtil.setContentLength(response, fileLength);
        HttpTool.setContentTypeHeader(response, file);
        HttpTool.setDateAndCacheHeaders(response, file);

        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (fileChannel.read(buffer) != -1) {
            buffer.flip();
            response.content().writeBytes(buffer);
            buffer.clear();
        }
        fileChannel.close();

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
