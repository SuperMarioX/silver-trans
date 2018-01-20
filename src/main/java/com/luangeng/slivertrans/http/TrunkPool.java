package com.luangeng.slivertrans.http;

import com.luangeng.slivertrans.model.AppConst;
import com.luangeng.slivertrans.model.ChunkInfo;
import io.netty.buffer.ByteBuf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TrunkPool {

    private static TrunkPool pool = new TrunkPool();

    private static Map<String, Task> taskMap = new HashMap();

    public static TrunkPool instance() {
        return pool;
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

    public synchronized boolean indexDone(ChunkInfo info) {
        Task t = taskMap.get(info.getResumableIdentifier());
        if (t != null) {
            return t.indexs.contains(info.getResumableChunkNumber());
        }
        return false;
    }

    private static class Task {
        private File f;
        private RandomAccessFile raf = null;
        private Set<Long> indexs = new HashSet<>();

        public Task(ChunkInfo info) {
            f = new File(AppConst.ROOT + File.separator + info.getResumableFilename() + ".temp");
            try {
                raf = new RandomAccessFile(f, "rw");
            } catch (FileNotFoundException e) {
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
                    String newPath = AppConst.ROOT + File.separator + info.getResumableFilename();
                    f.renameTo(new File(newPath));
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

    }

}
