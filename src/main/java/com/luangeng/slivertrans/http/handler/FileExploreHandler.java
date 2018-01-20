package com.luangeng.slivertrans.http.handler;

import com.google.gson.Gson;
import com.luangeng.slivertrans.model.AppConst;
import com.luangeng.slivertrans.model.ListFile;
import com.luangeng.slivertrans.tools.HttpTool;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URLDecoder;
import java.util.regex.Pattern;

public class FileExploreHandler extends AbstractHttpHandler {

    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[^-\\._]?[^<>&\\\"]*");

    @Override
    protected ChannelFuture handle(ChannelHandlerContext ctx, FullHttpRequest request, String uri) throws Exception {
        uri = URLDecoder.decode(uri, "UTF8");
        String path = HttpTool.getParams(uri).get("path");
        if (path.endsWith("...")) {
            path = path.substring(0, path.lastIndexOf("/"));
        }
        path = AppConst.ROOT + path;
        if (!path.startsWith(AppConst.ROOT)) {
            path = AppConst.ROOT;
        }

        File file = new File(path);
        if (!file.exists() || file.isHidden()) {
            HttpTool.sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return null;
        }
        if (file.isDirectory()) {
            ListFile lf = new ListFile();
            lf.setPath(path.substring(AppConst.ROOT.length()));
            for (File f : file.listFiles(ff -> ff.isDirectory())) {
                if (f.isHidden() || !f.canRead()) {
                    continue;
                }
                String name = f.getName();
                if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
                    continue;
                }
                lf.getDirs().add(name);
            }
            for (File f : file.listFiles(ff -> ff.isFile())) {
                if (f.isHidden() || !f.canRead()) {
                    continue;
                }
                String name = f.getName();
                if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
                    continue;
                }
                lf.getFiles().add(name);
            }

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            ByteBuf buffer = Unpooled.copiedBuffer(new Gson().toJson(lf), CharsetUtil.UTF_8);
            HttpUtil.setContentLength(response, buffer.readableBytes());
            response.content().writeBytes(buffer);
            buffer.release();
            return ctx.writeAndFlush(response);

        } else if (file.isFile()) {

            if (!file.canRead()) {
                HttpTool.sendError(ctx, HttpResponseStatus.FORBIDDEN);
                return null;
            }
            //
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            long length = raf.length();
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            HttpUtil.setContentLength(response, length);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");
            // Header中只支持ASCII, ISO-8859-1编码是单字节编码，向下兼容ASCII
            String filename = new String(file.getName().getBytes(), "ISO-8859-1");
            response.headers().set(HttpHeaderNames.CONTENT_DISPOSITION, "attachment;filename=" + filename);
            HttpTool.setDateAndCacheHeaders(response, file);
            // Write the initial line and the header.
            ctx.write(response);

            // Write the content.
            ChannelFuture sendFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, length), ctx.newProgressivePromise());
            ChannelFuture lastFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

            sendFuture.addListener(new ChannelProgressiveFutureListener() {
                @Override
                public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
                }

                @Override
                public void operationComplete(ChannelProgressiveFuture future) {
                    //System.err.println(future.channel() + " Transfer complete.");
                    try {
                        raf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            return lastFuture;
        }

        return null;
    }
}
