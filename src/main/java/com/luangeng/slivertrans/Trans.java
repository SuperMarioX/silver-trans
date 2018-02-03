package com.luangeng.slivertrans;

import com.luangeng.slivertrans.client.TransClient;
import com.luangeng.slivertrans.model.CmdEnum;
import com.luangeng.slivertrans.tools.TransTool;
import io.netty.channel.Channel;

import java.util.Arrays;

public class Trans implements TransApi {

    @Override
    public void upload(String path) {

    }

    @Override
    public void download(String location) {
        String[] params = location.split(":");
        Arrays.stream(params).forEach(t -> t.trim());
        if (params.length == 3) {
            String ip = params[0];
            Integer port = Integer.valueOf(params[1]);
            String path = params[2];
            Channel channel = TransClient.instance().connect(ip, port);
            TransTool.sendCmd(channel, CmdEnum.GET, path);
        } else {

        }
    }

    @Override
    public void delete(String location) {
        String[] params = location.split(":");
        Arrays.stream(params).forEach(t -> t.trim());
        if (params.length == 3) {
            String ip = params[0];
            Integer port = Integer.valueOf(params[1]);
            String path = params[2];
            Channel channel = TransClient.instance().connect(ip, port);
            TransTool.sendCmd(channel, CmdEnum.DELETE, path);
        } else {

        }
    }
}
