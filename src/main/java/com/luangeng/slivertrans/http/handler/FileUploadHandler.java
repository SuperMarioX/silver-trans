package com.luangeng.slivertrans.http.handler;

import com.luangeng.slivertrans.http.TrunkReceiver;
import com.luangeng.slivertrans.model.ChunkInfo;
import com.luangeng.slivertrans.tools.HttpTool;
import com.luangeng.slivertrans.tools.StringTool;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;

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
            return null;
        }
        return info;
    }

    @Override
    protected ChannelFuture handle(ChannelHandlerContext ctx, FullHttpRequest request, String uri) throws Exception {

        ChunkInfo info = getResumableInfo(uri);
        if (info == null) {
            HttpTool.sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return null;
        }

        ChannelFuture channelFuture = null;
        if (request.method() == HttpMethod.GET) {
            if (TrunkReceiver.instance().chunkWriten(info)) {
                HttpTool.sendMsg(ctx, "Uploaded");
            } else {
                HttpTool.sendNotModified(ctx);
            }
            return null;
        } else if (request.method() == HttpMethod.POST) {

            boolean done = TrunkReceiver.instance().write(info, request.content());

            if (done) {
                channelFuture = HttpTool.sendMsg(ctx, "All finished.");
            } else {
                channelFuture = HttpTool.sendMsg(ctx, "Upload");
            }
        }

        return channelFuture;
    }


}
