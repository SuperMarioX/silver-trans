package com.luangeng;

import com.luangeng.client.TransClient;
import com.luangeng.server.TransServer;
import com.luangeng.support.ConfigTool;

public class CmdApp {

    public static void main(String[] args) {

        String mode = ConfigTool.getValue("mode");
        String ip = ConfigTool.getValue("server.ip");
        int port = ConfigTool.getInt("server.port");

        if (mode == null) {
            System.out.println("error");
        } else if (mode.equals("server")) {
            TransServer.instance().start(port);
        } else if (mode.equals("client")) {
            TransClient.instance().start(ip, port);
            TransClient.instance().readCmd();
        } else if (mode.equals("both")) {
            TransServer.instance().start(port);

            TransClient.instance().start(ip, port);
            TransClient.instance().readCmd();
        }

    }
}
