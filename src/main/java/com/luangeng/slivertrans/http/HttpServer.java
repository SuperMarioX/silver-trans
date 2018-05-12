package com.luangeng.slivertrans.http;

import com.luangeng.slivertrans.http.handler.HttpBaseHandler;
import com.luangeng.slivertrans.server.TransServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.State.NEW;

/*
    HTTP Server
 */
public class HttpServer extends Thread {

    private static Logger logger = LoggerFactory.getLogger(TransServer.class);

    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;

    private static HttpServer server = new HttpServer();

    private int port;

    public static HttpServer instance() {
        return server;
    }

    public void start(int port) {
        if (this.getState() == NEW) {
            this.port = port;
            this.bossGroup = new NioEventLoopGroup(1);
            this.workerGroup = new NioEventLoopGroup();
            this.start();
        }
    }

    public void run() {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            //b.handler(new LoggingHandler(LogLevel.INFO));
            b.childHandler(new HttpServerChannelInitializer());

            Channel ch = b.bind(port).sync().channel();
            logger.info("HTTP Server started on port: " + port);

            //ch.closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void shutdown() {
        if (bossGroup != null && workerGroup != null) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(SocketChannel ch) {
            ChannelPipeline pipeline = ch.pipeline();
            //pipeline.addLast(new HttpRequestDecoder());
            //pipeline.addLast(new HttpResponseEncoder());
            // Remove the following line if you don't want automatic content compression.
            pipeline.addLast(new HttpContentCompressor());

            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
            pipeline.addLast(new ChunkedWriteHandler());

            pipeline.addLast(new HttpBaseHandler());
        }
    }
}
