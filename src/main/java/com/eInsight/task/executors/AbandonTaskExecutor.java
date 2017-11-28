package com.eInsight.task.executors;

import com.eInsight.task.common.TaskLogUtils;

public class AbandonTaskExecutor extends TaskExecutor {
    private static volatile AbandonTaskExecutor instance = null;

    public static AbandonTaskExecutor getInstance() {
        if (instance == null) {
            synchronized (AbandonTaskExecutor.class) {
                if (instance == null) {
                    instance = new AbandonTaskExecutor();
                }
            }
        }
        return instance;
    }

    public void execute() {
        ExecutorJob job = null;
        try {
            while (true) {
                job = this.taskQueue.take();
                this.taskTake += 1L;
                job.setExecuteStatus(ExecutorJob.ExecuteStatusEnum.ABANDON.name());
                this.done_success += 1L;
            }
        } catch (Exception ex) {
            this.done_success_abandon += 1L;
            TaskLogUtils.recordErrorInfo(ex, job);
        }
    }
}
