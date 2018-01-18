package com.luangeng.slivertrans.model;

import java.util.HashMap;

public class ResumableInfoStorage {

    private static ResumableInfoStorage sInstance = new ResumableInfoStorage();
    //resumableIdentifier --  ResumableInfo
    private HashMap<String, ResumableInfo> mMap = new HashMap<String, ResumableInfo>();

    //Single instance
    private ResumableInfoStorage() {
    }

    public static ResumableInfoStorage getInstance() {
        return sInstance;
    }

    /**
     * Get ResumableInfo from mMap or Create a new one.
     *
     * @param resumableChunkSize
     * @param resumableTotalSize
     * @param resumableIdentifier
     * @param resumableFilename
     * @param resumableRelativePath
     * @param resumableFilePath
     * @return
     */
    public synchronized ResumableInfo get(int resumableChunkSize, long resumableTotalSize,
                                          String resumableIdentifier, String resumableFilename,
                                          String resumableRelativePath, String resumableFilePath) {

        ResumableInfo info = mMap.get(resumableIdentifier);

        if (info == null) {
            info = new ResumableInfo();

            info.resumableChunkSize = resumableChunkSize;
            info.resumableTotalSize = resumableTotalSize;
            info.resumableIdentifier = resumableIdentifier;
            info.resumableFilename = resumableFilename;
            info.resumableRelativePath = resumableRelativePath;
            info.resumableFilePath = resumableFilePath;

            mMap.put(resumableIdentifier, info);
        }
        return info;
    }

    /**
     * @param info
     */
    public synchronized void remove(ResumableInfo info) {
        mMap.remove(info.resumableIdentifier);
    }

}
