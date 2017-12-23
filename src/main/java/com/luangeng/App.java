package com.luangeng;

import com.luangeng.cmd.CmdClient;
import com.luangeng.cmd.CmdServer;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        String ip = AppConfig.getValue("server.ip");
        int port = Integer.valueOf(AppConfig.getValue("server.port"));

        String mode = AppConfig.getValue("mode");
        if (mode.equals("server")) {
            new CmdServer(port).start();
            //TransServer.start();
        } else if (mode.equals("client")) {
            new CmdClient(ip, port).start();
            //TransClient.start();
        }
    }
}
