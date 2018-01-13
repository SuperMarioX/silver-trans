package com.luangeng;

import com.luangeng.client.TransClient;
import com.luangeng.server.TransServer;
import com.luangeng.support.ConfigTool;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;

public class CmdApp {

    public static void main(String[] args) {

        PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "config" + File.separator + "log4j.properties");

        int port = ConfigTool.getInt("server.port");
        TransServer.instance().startup(port);

        String clientIp = ConfigTool.getValue("client.ip");
        String clientPort = ConfigTool.getValue("client.port");
        if (clientIp != null && clientPort != null) {
            TransClient.instance().startup(clientIp, Integer.valueOf(clientPort));
            TransClient.instance().readCmd();
        }

        Runtime.getRuntime().addShutdownHook(new ShutDownThread());
    }

    private static class ShutDownThread extends Thread {
        public void run() {
            TransClient.instance().shutdown();
            TransServer.instance().shutdown();
        }
    }
}
