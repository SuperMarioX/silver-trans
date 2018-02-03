package com.luangeng.slivertrans.client;

import com.luangeng.slivertrans.model.AppConst;
import com.luangeng.slivertrans.model.TransData;
import com.luangeng.slivertrans.model.TypeEnum;
import com.luangeng.slivertrans.support.FileReceiverPool;
import com.luangeng.slivertrans.tools.TransTool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ClientHandler extends SimpleChannelInboundHandler<TransData> {

    private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private static Map<Integer, String> map = new HashMap();

    public static String getFileNameByIndex(int i) {
        return map.get(Integer.valueOf(i));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TransData data) throws Exception {
        TypeEnum type = data.getType();
        if (type == TypeEnum.MSG) {
            String msg = TransTool.getMsg(data);
            if (msg.startsWith("ls ")) {
                praseLs(msg);
            } else {
                logger.info(TransTool.getMsg(data));
            }
        } else if (type == TypeEnum.DATA || type == TypeEnum.END || type == TypeEnum.BEGIN) {
            FileReceiverPool.receive(data);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.info(cause.getMessage());
    }

    private void praseLs(String msg) {
        map.clear();
        String ss = msg.substring(3).trim();
        String[] paths = ss.split("\n");
        for (String p : paths) {
            p = p.trim();
            String[] dd = p.split(AppConst.DELIMITER);
            if (dd.length == 3) {
                logger.info(dd[0] + " " + dd[1] + " " + dd[2]);
                map.put(Integer.valueOf(dd[0].trim()), dd[2].trim());
            }
        }
    }

}
