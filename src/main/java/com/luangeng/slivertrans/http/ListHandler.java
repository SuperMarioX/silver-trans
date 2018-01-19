package com.luangeng.slivertrans.http;

import com.luangeng.slivertrans.model.AppConst;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ListHandler extends HttpHandler {

    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[^-\\._]?[^<>&\\\"]*");

    private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
        response.headers().set(HttpHeaderNames.LOCATION, newUri);

        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public static Map<String, String> getParams(String uri) {
        Map<String, String> map = new HashMap<>();
        int i = uri.indexOf("?");
        if (i == -1) {
            return map;
        }
        uri = uri.substring(i + 1);
        String[] pair = uri.split("&");
        for (String str : pair) {
            String[] pp = str.split("=");
            if (pp.length == 2) {
                map.put(pp[0], pp[1]);
            } else if (pp.length == 1) {
                map.put(pp[0], "");
            } else {
                //error
                return null;
            }
        }
        return map;
    }

    @Override
    protected ChannelFuture handle(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response, String uri) throws Exception {
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        uri = URLDecoder.decode(uri, "UTF8");
        String path = getParams(uri).get("path");
        if (path == null || path.equals("/")) {
            path = AppConst.BASE_DIR;
        } else if (path.endsWith("..")) {
            path = path.substring(0, path.lastIndexOf("/"));
        } else {
            path = AppConst.BASE_DIR + File.separator + path;
        }
        System.out.println(path);
        File file = new File(path);
        if (!file.exists() || file.isHidden()) {
            HttpCommon.sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return null;
        }
        if (file.isDirectory()) {
            StringBuilder sb = new StringBuilder("{\"path\":\"" + path + "\",\"list\":[");
            for (File f : file.listFiles()) {
                if (f.isHidden() || !f.canRead()) {
                    continue;
                }

                String name = f.getName();
                if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
                    continue;
                }

                sb.append("\"" + name + "\"" + ",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]}");

            ByteBuf buffer = Unpooled.copiedBuffer(sb.toString(), CharsetUtil.UTF_8);
            HttpUtil.setContentLength(response, buffer.readableBytes());
            response.content().writeBytes(buffer);
            buffer.release();

            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {

        }
        return null;
    }
}
