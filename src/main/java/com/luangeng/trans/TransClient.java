package com.luangeng.trans;

import com.luangeng.AppConfig;
import com.luangeng.CmdTool;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.Scanner;

/**
 * Created by LG on 2017/9/30.
 */
public class TransClient {

    public static final void start() {
        String HOST = AppConfig.getValue("server.ip");
        int PORT = Integer.valueOf(AppConfig.getValue("server.port"));

        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            bootstrap.group(group).channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 4));
                    pipeline.addLast(new LengthFieldPrepender(4));
                    pipeline.addLast(new TransClientHandler());
                }
            });
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = bootstrap.connect(HOST, PORT).sync();
            System.out.println("Client started.");

            Scanner sc = new Scanner(System.in);
            while (sc.hasNextLine()) {
                String cmd = sc.nextLine().trim();
                if (cmd.equalsIgnoreCase("exit")) {
                    return;
                }
                if (cmd.startsWith("get ")) {
                    int i = Integer.valueOf(cmd.substring(4).trim());
                    cmd = "get " + TransClientHandler.getName(i);
                }
                CmdTool.sendMsg(f.channel(), cmd);
                System.out.println(cmd);
            }

            //f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            System.out.println("Client stoped.");
        }
    }

    public static void main(String[] args) {
        TransClient.start();
    }


}
