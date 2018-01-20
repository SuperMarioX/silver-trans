package com.luangeng.slivertrans.server;

import com.luangeng.slivertrans.model.AppConst;
import com.luangeng.slivertrans.tools.StringTool;
import com.luangeng.slivertrans.tools.TransTool;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Sender implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(Sender.class);

    String name;
    File f;
    FileInputStream in;
    ByteBuffer bf;
    //MappedByteBuffer mb;
    int index = 0;
    Channel channel;
    private long t0;

    public Sender(String path, String name, Channel c) {
        this.name = name;
        this.channel = c;
        this.f = new File(path + File.separator + name);
    }

    @Override
    public void run() {
        if (!f.exists() || !f.isFile() || !f.canRead()) {
            TransTool.sendMsg(channel, "File can not read.");
            return;
        }

        try {
            t0 = System.currentTimeMillis();
            bf = ByteBuffer.allocate(AppConst.BUFFER_SIZE);
            in = new FileInputStream(f);
            logger.info("Sending: " + name + "  Size: " + StringTool.size(f.length()));
            TransTool.sendBegin(channel, name + AppConst.DELIMITER + f.length());

            while (in.getChannel().read(bf) != -1) {
                //mb = ch.map(FileChannel.MapMode.READ_ONLY,0, 1024);
                bf.flip();
                TransTool.sendData(channel, bf, index);
                index++;
                bf.clear();
            }
            TransTool.sendEnd(channel, index);
            long cost = Math.round((System.currentTimeMillis() - t0) / 1000f);
            logger.info("Send complete: " + f.getName() + "   Cost Time: " + cost + "s");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            clear();
        }

    }

    private void clear() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        f = null;
        in = null;
    }

}
