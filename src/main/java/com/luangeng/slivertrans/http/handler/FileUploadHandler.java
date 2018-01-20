package com.luangeng.slivertrans.http.handler;

import com.luangeng.slivertrans.http.TrunkPool;
import com.luangeng.slivertrans.model.ChunkInfo;
import com.luangeng.slivertrans.tools.HttpTool;
import com.luangeng.slivertrans.tools.StringTool;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class FileUploadHandler extends AbstractHttpHandler {

    private ChunkInfo getResumableInfo(String uri) {
        long chunkNum = StringTool.toLong(HttpTool.getParams(uri).get("resumableChunkNumber"), -1);
        long chunkSize = StringTool.toLong(HttpTool.getParams(uri).get("resumableChunkSize"), -1);
        long currentChunkSize = StringTool.toLong(HttpTool.getParams(uri).get("resumableCurrentChunkSize"), -1);
        long totalSize = StringTool.toLong(HttpTool.getParams(uri).get("resumableTotalSize"), -1);
        long totalChunks = StringTool.toLong(HttpTool.getParams(uri).get("resumableTotalChunks"), -1);
        String identifier = HttpTool.getParams(uri).get("resumableIdentifier");
        String filename = HttpTool.getParams(uri).get("resumableFilename");
        String relativePath = HttpTool.getParams(uri).get("resumableRelativePath");
        String type = HttpTool.getParams(uri).get("resumableType");

        ChunkInfo info = new ChunkInfo();
        info.setResumableChunkNumber(chunkNum);
        info.setResumableChunkSize(chunkSize);
        info.setResumableCurrentChunkSize(currentChunkSize);
        info.setResumableFilename(filename);
        info.setResumableIdentifier(identifier);
        info.setResumableRelativePath(relativePath);
        info.setResumableTotalChunks(totalChunks);
        info.setResumableTotalSize(totalSize);
        info.setResumableType(type);

        if (!info.vaild()) {

        }
        return info;
    }

    @Override
    protected ChannelFuture handle(ChannelHandlerContext ctx, FullHttpRequest request, String uri) throws Exception {

        ChunkInfo info = getResumableInfo(uri);

        if (request.method() == HttpMethod.GET) {
            if (TrunkPool.instance().indexDone(info)) {
                writeResponse(ctx, "Uploaded");
            } else {
                HttpTool.sendError(ctx, HttpResponseStatus.NOT_MODIFIED);
            }
            return null;
        }

        boolean done = TrunkPool.instance().write(info, request.content());
        if (done) {
            writeResponse(ctx, "All finished.");
        } else {
            writeResponse(ctx, "Upload");
        }

        return null;
    }

    private void writeResponse(ChannelHandlerContext ctx, String msg) {
        ByteBuf bf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, bf);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, bf.readableBytes());
        ctx.writeAndFlush(response);
    }
}
