package com.luangeng.slivertrans.model;

import com.luangeng.slivertrans.tools.ConfigTool;

import java.io.File;
import java.util.regex.Pattern;

public interface AppConst {

    String DELIMITER = ":";

    int DEFAULT_PORT = 9000;

    int BUFFER_SIZE = 10240;

    String ASSETS_DIR = System.getProperty("user.dir") + File.separator + "assets";

    String ROOT = ConfigTool.getValue("root.path");

    Pattern ALLOWED_FILE_NAME = Pattern.compile("[^-\\._]?[^<>&\\\"]*");

}
