package com.luangeng.slivertrans.model;

import java.util.ArrayList;
import java.util.List;

public class ListFile {

    private List<Detail> dirs = new ArrayList<>();

    private String path;
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
}
