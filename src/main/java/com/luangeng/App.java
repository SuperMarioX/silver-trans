package com.luangeng;

import com.luangeng.trans.TransClient;
import com.luangeng.trans.TransServer;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        String mode = AppConfig.getValue("mode");
        if (mode.equals("server")) {
            TransServer.start();
        } else if (mode.equals("client")) {
            TransClient.start();
        }
    }
}
