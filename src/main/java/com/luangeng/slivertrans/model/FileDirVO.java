package com.luangeng.slivertrans.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回前台的目录下文件信息对象
 */
public class FileDirVO {

    private String path;

    private List<Detail> dirs = new ArrayList<>();

    private List<Detail> files = new ArrayList<>();


    public List<Detail> getDirs() {
        return dirs;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDirs(List<Detail> dirs) {
        this.dirs = dirs;
    }

    public List<Detail> getFiles() {
        return files;
    }

    public void setFiles(List<Detail> files) {
        this.files = files;
    }

    public static class Detail {
        private String name;
        private String size;
        private String date;

        public Detail(String name, String size, String date) {
            this.name = name;
            this.size = size;
            this.date = date;
        }
    }

    @Override
    public String toString() {
        return "FileDirVO{" +
                "path='" + path + '\'' +
                '}';
    }
}
