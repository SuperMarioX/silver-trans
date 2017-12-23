package com.luangeng;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by LG on 2017/9/23.
 */
public class ConfigTool {

    private static final String CONFIG_PATH = System.getProperty("user.dir") + File.separator + "config" + File.separator + "app.properties";

    //private static final String CONFIG_PATH = "/Users/LG/Dev/silver-trans/src/main/resources/app.properties";

    private static Properties ppt = new Properties();

    static {
        try {
            ppt.load(new FileInputStream(CONFIG_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reload() {
        ppt.clear();
        try {
            ppt.load(new FileInputStream(CONFIG_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getValue(String key) {
        key = key.trim();
        if (ppt.containsKey(key)) {
            String value = ppt.getProperty(key).trim();
            if ("".equals(value)) {
                return null;
            }
            return value;
        }
        return null;
    }

    public static int getInt(String key) {
        String s = getValue(key);
        return Integer.valueOf(s);
    }


}
