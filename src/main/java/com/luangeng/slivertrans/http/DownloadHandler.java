package com.luangeng.slivertrans.http;

import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;

public class DownloadHandler {

    public void aa() {
        // Write the content.
//        ChannelFuture sendFileFuture = null;
//        ChannelFuture lastContentFuture;
//        if (ctx.pipeline().get(SslHandler.class) == null) {
//            FileRegion fr = new DefaultFileRegion(raf.getChannel(), 0, fileLength);
//            sendFileFuture = ctx.writeAndFlush(fr, ctx.newProgressivePromise());
//            // Write the end marker.
//            lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
//        } else {
//            HttpChunkedInput ci = new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192));
//            sendFileFuture = ctx.writeAndFlush(ci, ctx.newProgressivePromise());
//            // HttpChunkedInput will write the end marker (LastHttpContent) for us.
//            lastContentFuture = sendFileFuture;
//        }

        //if (fileLength >= 10 * 1024 * 1024)
        //sendFileFuture.addListener(new MyChannelProgressiveFutureListener());
    }


    private static class MyChannelProgressiveFutureListener implements ChannelProgressiveFutureListener {
        @Override
        public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
            if (total < 0) {
                System.err.println(future.channel() + " Transfer progress: " + progress);
            } else {
                System.err.println(future.channel() + " Transfer progress: " + progress + " / " + total);
            }
        }

        @Override
        public void operationComplete(ChannelProgressiveFuture future) throws Exception {
            System.err.println(future.channel() + " Transfer complete.");
        }
    }
}
