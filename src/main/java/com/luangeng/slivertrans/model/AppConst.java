package com.luangeng.slivertrans.model;

import com.luangeng.slivertrans.tools.ConfigTool;

import java.io.File;

public class AppConst {

    public static final String DELIMITER = ":";

    public static final int DEFAULT_PORT = 9000;

    public static final int BUFFER_SIZE = 10240;

    public static final String CONFIG_DIR = System.getProperty("user.dir") + File.separator + "config";

    public static final String ASSETS_DIR = System.getProperty("user.dir") + File.separator + "static";

    public static final String DEFAULT_ROOT_PATH = System.getProperty("user.home");

    public static String ROOT_PATH = ConfigTool.getValue("root.path");

    static {
        if (ROOT_PATH == null) {
            ROOT_PATH = DEFAULT_ROOT_PATH;
        }
        File f = new File(ROOT_PATH);
        if (!f.exists() || !f.isDirectory()) {
            ROOT_PATH = DEFAULT_ROOT_PATH;
        }
    }

}
