package com.luangeng;

import com.luangeng.client.TransClient;
import com.luangeng.server.TransServer;

public class TransApi {

    public void start(String ip, int port) {
        TransServer.instance().start(port);
        TransClient.instance().start(ip, port);
    }

    public void get() {

    }

}
