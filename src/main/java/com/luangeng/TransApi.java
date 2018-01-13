package com.luangeng;

import com.luangeng.client.TransClient;
import com.luangeng.server.TransServer;

public class TransApi {

    public void start(String ip, int port) {
        TransServer.instance().startup(port);
        TransClient.instance().startup(ip, port);
    }

    public void get(String path) {
        TransClient.instance().getFile(path);
    }

}
