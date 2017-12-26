package com.luangeng;

import com.luangeng.cmd.CmdClient;
import com.luangeng.cmd.CmdServer;
import com.luangeng.support.ConfigTool;
import com.luangeng.trans.TransClient;
import com.luangeng.trans.TransServer;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        String ip = ConfigTool.getValue("server.ip");
        int port = ConfigTool.getInt("server.port");
        int port2 = ConfigTool.getInt("server.port2");

        String mode = ConfigTool.getValue("mode");
        if (mode.equals("server")) {
            new CmdServer(port).start();
            new TransServer(port2).start();
        } else if (mode.equals("client")) {
            new CmdClient(ip, port).start();
            TransClient.instance().start(ip, port2);
        }
    }
}
