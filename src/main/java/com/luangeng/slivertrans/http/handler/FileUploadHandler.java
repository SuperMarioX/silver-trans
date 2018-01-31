package com.luangeng.slivertrans.http.handler;

import com.luangeng.slivertrans.http.TrunkReceiver;
import com.luangeng.slivertrans.model.FileChunkInfo;
import com.luangeng.slivertrans.tools.HttpTool;
import com.luangeng.slivertrans.tools.StringTool;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.Map;

public class FileUploadHandler extends AbstractHttpHandler {

    private FileChunkInfo getResumableInfo(String uri) {
        Map<String, String> queryparams = HttpTool.getParams(uri);
        long chunkNum = StringTool.toLong(queryparams.get("resumableChunkNumber"), -1);
        long chunkSize = StringTool.toLong(queryparams.get("resumableChunkSize"), -1);
        long currentChunkSize = StringTool.toLong(queryparams.get("resumableCurrentChunkSize"), -1);
        long totalSize = StringTool.toLong(queryparams.get("resumableTotalSize"), -1);
        long totalChunks = StringTool.toLong(queryparams.get("resumableTotalChunks"), -1);
        String identifier = queryparams.get("resumableIdentifier");
        String filename = queryparams.get("resumableFilename");
        String relativePath = queryparams.get("resumableRelativePath");
        String type = queryparams.get("resumableType");

        FileChunkInfo info = new FileChunkInfo();
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

        FileChunkInfo info = getResumableInfo(uri);
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
