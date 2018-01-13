package com.luangeng.slivertrans;

import com.luangeng.slivertrans.client.TransClient;
import com.luangeng.slivertrans.server.TransServer;
import com.luangeng.slivertrans.support.ConfigTool;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class CmdApp {

    public static void main(String[] args) {

        String log4j = System.getProperty("user.dir") + File.separator + "config" + File.separator + "log4j.properties";
        PropertyConfigurator.configure(log4j);

        int port = ConfigTool.getInt("server.port");
        TransServer.instance().startup(port);
        Runtime.getRuntime().addShutdownHook(new ShutDownServer());

        String clientIp = ConfigTool.getValue("client.ip");
        String clientPort = ConfigTool.getValue("client.port");
        if (clientIp != null) {
            TransClient.instance().startup(clientIp, Integer.valueOf(clientPort));
            Runtime.getRuntime().addShutdownHook(new ShutDownClient());
        }

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String cmd = sc.nextLine().trim().toLowerCase();
            if (cmd.startsWith("connect ")) {
                String url = cmd.substring(8).trim();
                String[] pp = url.split(":");
                Arrays.stream(pp).forEach(t -> t.trim());
                if (pp.length == 1) {
                    TransClient.instance().startup(pp[0], 9000);
                } else if (pp.length == 2) {
                    port = Integer.valueOf(pp[1]);
                    TransClient.instance().startup(pp[0], port);
                } else {
                    System.out.println("error input");
                }
            } else if (cmd.equals("disconnect")) {
                TransClient.instance().shutdown();
            } else if (cmd.equals("exit")) {
                TransClient.instance().shutdown();
                TransServer.instance().shutdown();
                sc.close();
            } else if(cmd.equals("help") || cmd.equals("?")){
                System.out.println("");
            } else {
                TransClient.instance().cmd(cmd);
            }
        }

    }

    private static class ShutDownServer extends Thread {
        public void run() {
            TransServer.instance().shutdown();
        }
    }

    private static class ShutDownClient extends Thread {
        public void run() {
            TransClient.instance().shutdown();
        }
    }
}
