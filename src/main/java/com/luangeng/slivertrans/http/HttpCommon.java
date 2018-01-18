package com.luangeng.slivertrans.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_MODIFIED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpCommon {

    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;
    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");  // INSECURE_URI.matcher(uri).matches()

    // Decode the path.
    public static String sanitizeUri(String uri) {
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (uri.isEmpty() || uri.charAt(0) != '/') {
            return null;
        }
        // Convert file separators.
        uri = uri.replace('/', File.separatorChar);
        if (uri.contains(File.separator + '.') ||
                uri.contains('.' + File.separator) ||
                uri.charAt(0) == '.' ||
                uri.charAt(uri.length() - 1) == '.') {
            return null;
        }

        return uri;
    }


    public static boolean isStatic(String uri) {
        uri = uri.toLowerCase();
        Set<String> set = new HashSet<>();
        set.add(".css");
        set.add(".jpg");
        set.add(".gif");
        set.add(".js");
        set.add(".bmp");
        set.add(".html");
        set.add(".txt");
        for (String s : set) {
            if (uri.endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public static void sendError(ChannelHandlerContext ctx,
                                 HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                status, Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * When file timestamp is the same as what the browser is sending up, send a "304 Not Modified"
     *
     * @param ctx Context
     */
    public static void sendNotModified(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_MODIFIED);
        setDateHeader(response);

        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * Sets the Date header for the HTTP response
     *
     * @param response HTTP response
     */
    private static void setDateHeader(FullHttpResponse response) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));
    }

    /**
     * Sets the Date and Cache headers for the HTTP Response
     *
     * @param response    HTTP response
     * @param fileToCache file to extract content type
     */
    public static void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.headers().set(HttpHeaderNames.EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.headers().set(
                HttpHeaderNames.LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }

    /**
     * Sets the content type header for the HTTP Response
     *
     * @param response HTTP response
     * @param file     file to extract content type
     */
    public static void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
        if (file.getPath().endsWith(".css")) {
            response.headers().set(CONTENT_TYPE, "text/css; charset=UTF-8");
        }
    }


}
