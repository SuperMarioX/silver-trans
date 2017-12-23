package com.luangeng;

import com.luangeng.cmd.CmdClient;
import com.luangeng.cmd.CmdServer;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        String ip = ConfigTool.getValue("server.ip");
        int port = Integer.valueOf(ConfigTool.getValue("server.port"));

        String mode = ConfigTool.getValue("mode");
        if (mode.equals("server")) {
            new CmdServer(port).start();
            //TransServer.start();
        } else if (mode.equals("client")) {
            new CmdClient(ip, port).start();
            //TransClient.start();
        }
    }
}
