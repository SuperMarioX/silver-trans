package com.luangeng.slivertrans.model;

import java.io.File;

public interface AppConst {

    String DELIMITER = ":";

    int DEFAULT_PORT = 9000;

    int BUFFER_SIZE = 10240;

    String BASE_DIR = System.getProperty("user.dir") + File.separator + "webapp";

}
