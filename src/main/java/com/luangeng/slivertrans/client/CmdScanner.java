package com.luangeng.slivertrans.client;

import com.luangeng.slivertrans.model.AppConst;
import com.luangeng.slivertrans.tools.TransTool;
import io.netty.channel.Channel;

import java.util.Arrays;
import java.util.Scanner;

public class CmdScanner {

    private static Channel channel;

    public static void inputScan(String ip, Integer port) {
        if (ip != null && port != null) {
            channel = TransClient.instance().connect(ip, port);
            TransTool.sendCmd(channel, "pwd");
            TransTool.sendCmd(channel, "ls");
        }

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String cmd = scanner.nextLine().trim().toLowerCase();
            if (cmd.startsWith("connect ")) {
                if (channel != null && channel.isActive()) {
                    System.out.println("Trans Client already connect with ");
                    continue;
                }
                String url = cmd.substring(8).trim();
                String[] params = url.split(":");
                Arrays.stream(params).forEach(t -> t.trim());
                if (params.length == 1) {
                    TransClient.instance().connect(params[0], AppConst.DEFAULT_PORT);
                } else if (params.length >= 2) {
                    port = Integer.valueOf(params[1]);
                    TransClient.instance().connect(params[0], port);
                } else {
                    System.out.println("Error Command");
                }
            } else if (cmd.equals("disconnect")) {
                try {
                    if (channel != null && channel.isActive()) {
                        channel.close().sync();
                        channel = null;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (cmd.equals("exit")) {
                break;
            } else if (cmd.equals("help") || cmd.equals("?")) {
                System.out.println("this is help.");
            } else {
                if (channel != null && channel.isActive()) {
                    sendCmd(cmd);
                } else {
                    System.out.println("Not connected.");
                }
            }
        }
        scanner.close();
    }

    private static void sendCmd(String cmd) {
        if (cmd.startsWith("get ")) {
            int i = Integer.valueOf(cmd.substring(4).trim());
            cmd = "get " + ClientHandler.getFileNameByIndex(i);
        } else if (cmd.startsWith("cd ")) {
            String p = cmd.substring(3).trim();
            try {
                int i = Integer.valueOf(p);
                cmd = "cd " + ClientHandler.getFileNameByIndex(i);
            } catch (Exception e) {
                //nothing
            }
        }
        TransTool.sendCmd(channel, cmd);
    }

}
