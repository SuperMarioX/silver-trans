package com.luangeng.slivertrans;

import com.luangeng.slivertrans.http.HttpServer;
import com.luangeng.slivertrans.model.AppConst;
import com.luangeng.slivertrans.server.TransServer;
import com.luangeng.slivertrans.tools.ConfigTool;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.luangeng.slivertrans.model.AppConst.CONFIG_DIR;

public class App {

    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        String log4j = CONFIG_DIR + File.separator + "log4j.properties";
        PropertyConfigurator.configure(log4j);

        Integer port = ConfigTool.getInt("server.port", AppConst.DEFAULT_PORT);
        logger.info("http server start up...");
        HttpServer.instance().start(port + 1);
        //TransServer.instance().start(port);
        Runtime.getRuntime().addShutdownHook(new ShutDownServer());

        String clientIp = ConfigTool.getValue("client.ip");
        Integer clientPort = ConfigTool.getInt("client.port", AppConst.DEFAULT_PORT);

        //CmdScanner.inputScan(clientIp, clientPort);

        //TransClient.instance().shutdown();
        //TransServer.instance().shutdown();
        HttpServer.instance().shutdown();
    }

    private static class ShutDownServer extends Thread {
        public void run() {
            TransServer.instance().shutdown();
            HttpServer.instance().shutdown();
        }
    }

    private static class ShutDownClient extends Thread {
        public void run() {

        }
    }

}
