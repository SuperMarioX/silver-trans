package com.luangeng.slivertrans.model;

import java.util.ArrayList;
import java.util.List;

public class ListFile {

    private String path;

    private List<String> dirs = new ArrayList<>();

    private List<String> files = new ArrayList<>();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getDirs() {
        return dirs;
    }

    public void setDirs(List<String> dirs) {
        this.dirs = dirs;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
