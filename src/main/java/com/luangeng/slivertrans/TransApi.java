package com.luangeng.slivertrans;

import com.luangeng.slivertrans.client.TransClient;
import com.luangeng.slivertrans.server.TransServer;

public class TransApi {

    public void start(String ip, int port) {
        TransServer.instance().startup(port);
        TransClient.instance().startup(ip, port);
    }

    public void get(String path) {
        TransClient.instance().getFile(path);
    }

}
