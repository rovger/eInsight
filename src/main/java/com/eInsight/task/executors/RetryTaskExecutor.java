package com.eInsight.task.executors;

import com.eInsight.task.common.TaskLogUtils;
import org.apache.log4j.Logger;

public class RetryTaskExecutor extends TaskExecutor {
    private static Logger s_logger = Logger.getLogger(RetryTaskExecutor.class);
    private static final int MAX_RETRY_COUNT = 4;
    private static volatile RetryTaskExecutor instance = null;

    public static RetryTaskExecutor getInstance() {
        if (instance == null) {
            synchronized (RetryTaskExecutor.class) {
                if (instance == null) {
                    instance = new RetryTaskExecutor();
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
                try {
                    job.getTask().run(job.getStartDate(), job.getEndDate());
                    job.addExecuteCount();
                    this.done_success += 1L;
                } catch (Exception ex) {
                    s_logger.error(this.getClass().getSimpleName() + ".1", ex);
                    job.addExecuteCount();
                    if (job.getExecuteCount() > MAX_RETRY_COUNT) {
                        AbandonTaskExecutor.getInstance().submitJob(job);
                    } else {
                        getInstance().submitJob(job);
                    }
                }
                Thread.sleep(1000L);
            }
        } catch (Exception ex) {
            TaskLogUtils.recordErrorInfo(ex, job);
        }
    }
}
