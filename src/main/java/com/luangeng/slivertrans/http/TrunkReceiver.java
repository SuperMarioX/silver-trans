package com.luangeng.slivertrans.http;

import com.luangeng.slivertrans.model.AppConst;
import com.luangeng.slivertrans.model.ChunkInfo;
import io.netty.buffer.ByteBuf;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TrunkReceiver {

    private static TrunkReceiver receive = new TrunkReceiver();

    private static Map<String, Task> taskMap = new HashMap();

    public static TrunkReceiver instance() {
        return receive;
    }

    public synchronized boolean write(ChunkInfo info, ByteBuf bf) {
        String id = info.getResumableIdentifier();
        Task task = taskMap.get(id);
        if (task == null) {
            task = new Task(info);
            taskMap.put(id, task);
        }
        return task.write(bf, info);
    }

    public synchronized boolean chunkWriten(ChunkInfo info) {
        Task t = taskMap.get(info.getResumableIdentifier());
        if (t != null) {
            return t.indexs.contains(info.getResumableChunkNumber());
        }
        return false;
    }

    private static class Task {
        private String relativePath;
        private File f;
        private RandomAccessFile raf = null;
        private Set<Long> indexs = new HashSet<>();

        public Task(ChunkInfo info) {
            relativePath = info.getResumableRelativePath();
            String dst = AppConst.ROOT + relativePath + File.separator + info.getResumableFilename();
            try {
                f = new File(dst + ".temp");
                if (!f.getCanonicalPath().startsWith(AppConst.ROOT)) {
                    throw new IOException("path forbidden");
                }
                raf = new RandomAccessFile(f, "rw");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean write(ByteBuf bf, ChunkInfo info) {
            if (indexs.contains(info.getResumableChunkNumber())) {
                return false;
            }
            try {
                raf.seek((info.getResumableChunkNumber() - 1) * info.getResumableChunkSize());
                raf.getChannel().write(bf.nioBuffer());
                indexs.add(info.getResumableChunkNumber());
                if (indexs.size() == info.getResumableTotalChunks()) {
                    raf.close();
                    taskMap.remove(info.getResumableIdentifier());
                    String newPath = AppConst.ROOT + relativePath + File.separator + info.getResumableFilename();
                    f.renameTo(new File(newPath));
                    indexs.clear();
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

    }

}