package com.luangeng.slivertrans.client;

import com.luangeng.slivertrans.model.AppConst;
import com.luangeng.slivertrans.model.CmdEnum;
import com.luangeng.slivertrans.tools.TransTool;
import io.netty.channel.Channel;

import java.util.Arrays;
import java.util.Scanner;

public class CmdScanner {

    private static Channel channel;

    private static String ip;

    private static int port;

    public static void inputScan(String ip0, Integer port0) {
        if (ip0 != null && port0 != null) {
            ip = ip0;
            port = port0;
            channel = TransClient.instance().connect(ip, port);
            TransTool.sendCmd(channel, CmdEnum.PWD, "pwd");
            TransTool.sendCmd(channel, CmdEnum.LS, "ls");
        }

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String cmd = scanner.nextLine().trim().toLowerCase();
            if (cmd.startsWith("connect ")) {
                if (isActive()) {
                    System.out.println("Trans Client already connect with " + ip + ":" + port);
                    continue;
                }
                String url = cmd.substring(8).trim();
                String[] params = url.split(":");
                Arrays.stream(params).forEach(t -> t.trim());

                if (params.length == 1) {
                    ip = params[0];
                    port = AppConst.DEFAULT_PORT;
                    channel = TransClient.instance().connect(ip, port);

                } else if (params.length == 2) {
                    ip = params[0];
                    port = Integer.valueOf(params[1]);
                    channel = TransClient.instance().connect(ip, port);

                } else {
                    System.out.println("Error Command");
                }

            } else if (cmd.equals("disconnect")) {
                try {
                    if (isActive()) {
                        channel.close().sync();
                        System.out.println("Disconnected with " + ip + ":" + port);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (cmd.equals("exit")) {
                break;
            } else if (cmd.equals("help") || cmd.equals("?")) {
                System.out.println("this is help.");
            } else {
                if (isActive()) {
                    otherCmd(cmd);
                } else {
                    System.out.println("Not connected.");
                }
            }
        }
        scanner.close();
    }

    private static void otherCmd(String cmd) {
        if (cmd.startsWith("get ")) {
            int i = Integer.valueOf(cmd.substring(4).trim());
            String path = ClientHandler.getFileNameByIndex(i);
            TransTool.sendCmd(channel, CmdEnum.GET, path);
        } else if (cmd.startsWith("cd ")) {
            try {
                int i = Integer.valueOf(cmd.substring(3).trim());
                String path = ClientHandler.getFileNameByIndex(i);
                TransTool.sendCmd(channel, CmdEnum.CD, path);
            } catch (Exception e) {
                //nothing
            }
        } else {
            TransTool.sendCmd(channel, CmdEnum.fromStr(cmd), "");
        }

    }

    private static boolean isActive() {
        return channel != null && channel.isActive();
    }

}
