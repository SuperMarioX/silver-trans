package com.luangeng.slivertrans.http;

import com.luangeng.slivertrans.model.ResumableInfo;
import com.luangeng.slivertrans.model.ResumableInfoStorage;
import com.luangeng.slivertrans.tools.HttpUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpUploadhandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public static final String UPLOAD_DIR = "upload_dir";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        printRequest(request);

        int resumableChunkNumber = getResumableChunkNumber(request);

        ResumableInfo info = getResumableInfo(request);

        RandomAccessFile raf = new RandomAccessFile(info.resumableFilePath, "rw");

        //Seek to position
        raf.seek((resumableChunkNumber - 1) * (long) info.resumableChunkSize);

        //Save to file
        InputStream is = null;//request.getInputStream();
        long readed = 0;
        long content_length = HttpUtil.getContentLength(request);
        byte[] bytes = new byte[1024 * 100];
        while (readed < content_length) {
            int r = is.read(bytes);
            if (r < 0) {
                break;
            }
            raf.write(bytes, 0, r);
            readed += r;
        }
        raf.close();

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        //Mark as uploaded.
        info.uploadedChunks.add(new ResumableInfo.ResumableChunkNumber(resumableChunkNumber));
        if (info.checkIfUploadFinished()) { //Check if all chunks uploaded, and change filename
            ResumableInfoStorage.getInstance().remove(info);
            writeResponse(response, "All finished.");
        } else {
            writeResponse(response, "Upload");
        }
    }

    public void doget(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        int resumableChunkNumber = getResumableChunkNumber(request);

        ResumableInfo info = getResumableInfo(request);

        if (info.uploadedChunks.contains(new ResumableInfo.ResumableChunkNumber(resumableChunkNumber))) {
            writeResponse(null, "Uploaded"); //This Chunk has been Uploaded.
        } else {
            response.setStatus(HttpResponseStatus.NOT_FOUND);
        }
    }

    private int getResumableChunkNumber(FullHttpRequest request) {
        return HttpUtils.toInt(getParam(request, "resumableChunkNumber"), -1);
    }

    private ResumableInfo getResumableInfo(FullHttpRequest request) throws Exception {
        String base_dir = UPLOAD_DIR;

        int resumableChunkSize = HttpUtils.toInt(getParam(request, "resumableChunkSize"), -1);
        long resumableTotalSize = HttpUtils.toLong(getParam(request, "resumableTotalSize"), -1);
        String resumableIdentifier = getParam(request, "resumableIdentifier");
        String resumableFilename = getParam(request, "resumableFilename");
        String resumableRelativePath = getParam(request, "resumableRelativePath");
        //Here we add a ".temp" to every upload file to indicate NON-FINISHED
        new File(base_dir).mkdir();
        String resumableFilePath = new File(base_dir, resumableFilename).getAbsolutePath() + ".temp";

        ResumableInfoStorage storage = ResumableInfoStorage.getInstance();

        ResumableInfo info = storage.get(resumableChunkSize, resumableTotalSize,
                resumableIdentifier, resumableFilename, resumableRelativePath, resumableFilePath);
        if (!info.vaild()) {
            storage.remove(info);
            throw new Exception("Invalid request params.");
        }
        return info;
    }

    private String getParam(FullHttpRequest request, String s) {
        return s;
    }

    private void writeResponse(FullHttpResponse response, String s) {
        response.content().writeBytes(s.getBytes());
    }

    private void printRequest(FullHttpRequest r) {
        System.out.println(r.headers());
        System.out.println(r.content());
        System.out.println(r.decoderResult());

    }
}
