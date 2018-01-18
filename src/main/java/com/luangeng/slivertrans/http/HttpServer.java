package com.luangeng.slivertrans.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpServer {

    static int PORT = 8080;

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.handler(new LoggingHandler(LogLevel.INFO));
            b.childHandler(new HttpUploadServerInitializer());

            Channel ch = b.bind(PORT).sync().channel();

            System.out.println("Open your web browser and navigate to " +
                    "http" + "://127.0.0.1:" + PORT + '/');

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static class HttpUploadServerInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(SocketChannel ch) {
            ChannelPipeline pipeline = ch.pipeline();
            //pipeline.addLast(new HttpRequestDecoder());
            //pipeline.addLast(new HttpResponseEncoder());
            // Remove the following line if you don't want automatic content compression.
            //pipeline.addLast(new HttpContentCompressor());

            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(65536));
            pipeline.addLast(new ChunkedWriteHandler());

            pipeline.addLast(new HttpBaseHandler());
        }
    }
}
