package com.luangeng.slivertrans.model;

public class ChunkInfo {

    //序列 从1开始
    private long resumableChunkNumber;

    //通常每块大小
    private long resumableChunkSize;

    //实际块大小
    private long resumableCurrentChunkSize;

    //文件大小
    private long resumableTotalSize;

    //application/zip
    private String resumableType;

    //唯一标识
    private String resumableIdentifier;

    //文件名
    private String resumableFilename;

    //相对路径
    private String resumableRelativePath;

    //块总数
    private long resumableTotalChunks;

    public long getResumableChunkNumber() {
        return resumableChunkNumber;
    }

    public void setResumableChunkNumber(long resumableChunkNumber) {
        this.resumableChunkNumber = resumableChunkNumber;
    }

    public long getResumableChunkSize() {
        return resumableChunkSize;
    }

    public void setResumableChunkSize(long resumableChunkSize) {
        this.resumableChunkSize = resumableChunkSize;
    }

    public long getResumableCurrentChunkSize() {
        return resumableCurrentChunkSize;
    }

    public void setResumableCurrentChunkSize(long resumableCurrentChunkSize) {
        this.resumableCurrentChunkSize = resumableCurrentChunkSize;
    }

    public long getResumableTotalSize() {
        return resumableTotalSize;
    }

    public void setResumableTotalSize(long resumableTotalSize) {
        this.resumableTotalSize = resumableTotalSize;
    }

    public String getResumableType() {
        return resumableType;
    }

    public void setResumableType(String resumableType) {
        this.resumableType = resumableType;
    }

    public String getResumableIdentifier() {
        return resumableIdentifier;
    }

    public void setResumableIdentifier(String resumableIdentifier) {
        this.resumableIdentifier = resumableIdentifier;
    }

    public String getResumableFilename() {
        return resumableFilename;
    }

    public void setResumableFilename(String resumableFilename) {
        this.resumableFilename = resumableFilename;
    }

    public String getResumableRelativePath() {
        return resumableRelativePath;
    }

    public void setResumableRelativePath(String resumableRelativePath) {
        this.resumableRelativePath = resumableRelativePath;
    }

    public long getResumableTotalChunks() {
        return resumableTotalChunks;
    }

    public void setResumableTotalChunks(long resumableTotalChunks) {
        this.resumableTotalChunks = resumableTotalChunks;
    }

    public boolean vaild() {
        if (resumableChunkNumber < 0 ||
                resumableChunkSize < 0 ||
                resumableTotalSize < 0 ||
                resumableCurrentChunkSize > resumableTotalSize ||
                resumableChunkNumber > resumableTotalChunks) {
            return false;
        } else {
            return true;
        }
    }

}
