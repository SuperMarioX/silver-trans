package com.luangeng.slivertrans;

import com.luangeng.slivertrans.client.TransClient;
import com.luangeng.slivertrans.server.TransServer;

import java.util.HashMap;
import java.util.Map;

public class TransApi {

    private static Map<String, TransClient> map = new HashMap();

    public static void startServer(int port) {
        TransServer.instance().start(port);
    }

    public static void getFile(String path) {
        String addr = path.substring(11);
        TransClient client = map.get(addr);
        if (client == null) {
            //client = new TransClient();
            //map.put(addr, client);
        }
        //client.getFile(path);
    }

}
