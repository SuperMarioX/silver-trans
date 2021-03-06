package com.luangeng.slivertrans.http.handler;

import com.google.gson.Gson;
import com.luangeng.slivertrans.model.AppConst;
import com.luangeng.slivertrans.model.FileDirVO;
import com.luangeng.slivertrans.tools.HttpTool;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.RandomAccessFile;

import static com.luangeng.slivertrans.tools.StringTool.formatDate;
import static com.luangeng.slivertrans.tools.StringTool.formatFileSize;

public class FileExploreHandler extends AbstractHttpHandler {

    private Gson gson = new Gson();

    @Override
    protected ChannelFuture handle(ChannelHandlerContext ctx, FullHttpRequest request, String uri) throws Exception {
        uri = uri.replace('/', File.separatorChar);
        String path = HttpTool.getParams(uri).get("path");
        path = path == null ? "" : path;
        if (path.endsWith("..")) {
            path = path.substring(0, path.lastIndexOf(File.separatorChar));
        }

        File file = new File(AppConst.ROOT_PATH + path);
        if (!file.getCanonicalPath().startsWith(AppConst.ROOT_PATH)) {
            HttpTool.sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return null;
        }

        path = file.getCanonicalPath();
        if (!file.exists()) {
            HttpTool.sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return null;
        }

        if (!file.canRead()) {
            HttpTool.sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return null;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                HttpTool.sendError(ctx, HttpResponseStatus.FORBIDDEN);
                return null;
            }

            FileDirVO fileDirVO = new FileDirVO();
            fileDirVO.setPath(path.substring(AppConst.ROOT_PATH.length()));
            for (File f : files) {
                if (!f.canRead()) {
                    continue;
                }
                if (f.isDirectory()) {
                    fileDirVO.getDirs().add(new FileDirVO.Detail(f.getName(), "", formatDate(f.lastModified())));
                } else {
                    fileDirVO.getFiles().add(new FileDirVO.Detail(f.getName(), formatFileSize(f.length()), formatDate(f.lastModified())));
                }
            }

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            ByteBuf buffer = Unpooled.copiedBuffer(gson.toJson(fileDirVO), CharsetUtil.UTF_8);
            HttpUtil.setContentLength(response, buffer.readableBytes());
            response.content().writeBytes(buffer);
            buffer.release();
            return ctx.writeAndFlush(response);

        } else if (file.isFile()) {

            RandomAccessFile raf = new RandomAccessFile(file, "r");
            long length = raf.length();
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            HttpUtil.setContentLength(response, length);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");
            // Header中只支持ASCII, ISO-8859-1编码是单字节编码，向下兼容ASCII
            String filename = new String(file.getName().getBytes(), "ISO-8859-1");
            response.headers().set(HttpHeaderNames.CONTENT_DISPOSITION, "attachment;filename=" + filename);
            HttpTool.setDateAndCacheHeaders(response, file);
            ctx.write(response);

            // Write the content.
            ctx.write(new DefaultFileRegion(raf.getChannel(), 0, length)).addListener((ChannelFutureListener) future -> raf.close());
            ChannelFuture lastFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

            return lastFuture;
        }

        return null;
    }
}
