package com.eInsight.task.common;

import com.eInsight.task.executors.ExecutorJob;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ThreadPoolsUtil {
    private static final int MAX_POOL_SIZE = 50;

    public void buildSubTasks(List<ExecutorJob> list, int batchSize, int poolSize)
            throws Exception {
        if (poolSize > 50) {
            throw new Exception("Exceed max allowed pool size 50.");
        }
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        int batchStart = 0;
        while (batchStart < list.size()) {
            int batchEnd = batchStart + batchSize;
            final List<ExecutorJob> subList = list.subList(batchStart, Math.min(batchEnd, list.size()));
            executor.execute(new Runnable() {
                public void run() {
                    ThreadPoolsUtil.this.process(subList);
                }
            });
            batchStart = batchEnd;
        }
        executor.shutdown();
    }

    public abstract void process(List<ExecutorJob> paramList);
}
