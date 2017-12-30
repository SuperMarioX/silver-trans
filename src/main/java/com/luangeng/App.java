package com.luangeng;

import com.luangeng.client.TransClient;
import com.luangeng.server.TransServer;
import com.luangeng.support.ConfigTool;

public class App {
    public static void main(String[] args) {

        String ip = ConfigTool.getValue("server.ip");
        int port = ConfigTool.getInt("server.port");

        String mode = ConfigTool.getValue("mode");
        if (mode == null) {
            System.out.println("error");
        } else if (mode.equals("server")) {
            TransServer server = new TransServer(port);
            server.start();
        } else if (mode.equals("client")) {
            TransClient.instance().start(ip, port);
        } else if (mode.equals("both")) {
            TransServer server = new TransServer(port);
            server.start();
            TransClient.instance().start(ip, port);
        }
    }
}
