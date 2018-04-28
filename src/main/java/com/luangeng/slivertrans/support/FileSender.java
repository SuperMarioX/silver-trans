package com.luangeng.slivertrans.support;

import com.luangeng.slivertrans.model.AppConst;
import com.luangeng.slivertrans.model.TransData;
import com.luangeng.slivertrans.model.TypeEnum;
import com.luangeng.slivertrans.tools.StringTool;
import com.luangeng.slivertrans.tools.TransTool;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static com.luangeng.slivertrans.tools.TransTool.CHARSET;

/*
    文件发送线程
 */
public class FileSender implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(FileSender.class);

    private String id;
    private String fileName;
    private File file;
    private Channel channel;

    public FileSender(String path, String fileName, Channel c) {
        this.fileName = fileName;
        this.channel = c;
        this.file = new File(path + File.separator + fileName);
    }

    @Override
    public void run() {
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            TransTool.sendMsg(channel, "File not exist or can not read.");
            return;
        }

        int index = 1;
        String identity = channel.localAddress().toString() + file.getAbsolutePath() + channel.remoteAddress().toString();
        id = Encrypt.MD5_Encrypt(identity);
        long t0 = System.currentTimeMillis();
        ByteBuffer bf = ByteBuffer.allocate(AppConst.BUFFER_SIZE);
        FileChannel fileChannel = null;

        try {
            fileChannel = new FileInputStream(file).getChannel();

            logger.info("Sending: " + fileName + "  Size: " + StringTool.formatFileSize(file.length()));
            sendBegin(channel, fileName + AppConst.DELIMITER + file.length());

            while (fileChannel.read(bf) != -1) {
                bf.flip();
                sendData(channel, bf, index);
                index++;
                bf.clear();
            }

            sendEnd(channel, index);
            long cost = Math.round((System.currentTimeMillis() - t0) / 1000f);
            logger.info("Send complete: " + file.getName() + "   Cost Time: " + cost + "s");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (fileChannel != null) {
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

    }

    public void sendBegin(Channel ch, String msg) {
        ByteBuf bfn = Unpooled.copiedBuffer(msg, CHARSET);
        TransData data = new TransData(TypeEnum.BEGIN, bfn);
        data.setId(id);
        data.setIndex(0);
        ch.writeAndFlush(data);
    }

    public void sendData(Channel ch, ByteBuffer bf, int index) {
        TransData data = new TransData();
        data.setId(id);
        data.setType(TypeEnum.DATA);
        ByteBuf bfn = Unpooled.copiedBuffer(bf);
        data.setData(bfn);
        data.setIndex(index);
        ch.writeAndFlush(data);
    }

    public void sendEnd(Channel ch, int index) {
        TransData data = new TransData(TypeEnum.END, Unpooled.EMPTY_BUFFER);
        data.setId(id);
        data.setIndex(index);
        ch.writeAndFlush(data);
    }

    @Override
    public String toString() {
        return "File name:" + fileName + "  ID: " + id;
    }

}
