package com.luangeng.slivertrans;

import com.luangeng.slivertrans.client.TransClient;
import com.luangeng.slivertrans.model.AppConst;
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

        Integer port = ConfigTool.getInt("server.port");
        if (port == null) {
            port = AppConst.DEFAULT_PORT;
        }
        TransServer.instance().start(port);
        Runtime.getRuntime().addShutdownHook(new ShutDownServer());

        TransClient client = null;
        String clientIp = ConfigTool.getValue("client.ip");
        Integer clientPort = ConfigTool.getInt("client.port");
        if (clientPort == null) {
            clientPort = AppConst.DEFAULT_PORT;
        }
        if (clientIp != null) {
            client = new TransClient();
            client.start(clientIp, Integer.valueOf(clientPort));
        }

        Runtime.getRuntime().addShutdownHook(new ShutDownClient(client));

        inputScan(client);

        if (client != null) {
            client.shutdown();
        }
        TransServer.instance().shutdown();
    }

    private static void inputScan(TransClient client) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String cmd = scanner.nextLine().trim().toLowerCase();
            if (cmd.startsWith("connect ")) {
                if (client != null) {
                    System.out.println("Trans Client already connect with " + client.getAddress());
                    continue;
                }
                String url = cmd.substring(8).trim();
                String[] pp = url.split(":");
                Arrays.stream(pp).forEach(t -> t.trim());
                if (pp.length == 1) {
                    client = new TransClient();
                    client.start(pp[0], AppConst.DEFAULT_PORT);
                } else if (pp.length == 2) {
                    int port = Integer.valueOf(pp[1]);
                    client = new TransClient();
                    client.start(pp[0], port);
                } else {
                    System.out.println("Error input");
                }
            } else if (cmd.equals("disconnect")) {
                client.shutdown();
                client = null;
            } else if (cmd.equals("exit")) {
                break;
            } else if (cmd.equals("help") || cmd.equals("?")) {
                System.out.println("this is help.");
            } else {
                if (client != null) {
                    client.sendCmd(cmd);
                } else {
                    System.out.println("Not connected.");
                }
            }
        }
        scanner.close();
    }

    private static class ShutDownServer extends Thread {
        public void run() {
            TransServer.instance().shutdown();
        }
    }

    private static class ShutDownClient extends Thread {
        private TransClient client;

        ShutDownClient(TransClient client) {
            this.client = client;
        }

        public void run() {
            if (client != null) {
                client.shutdown();
            }
        }
    }

}
