package com.eInsight.task.executors;

import com.eInsight.common.alert.common.entity.AlertEventType;
import com.eInsight.common.alert.mail.AlertMessage;
import com.eInsight.task.common.TaskLogUtils;
import com.eInsight.task.common.TaskUtil;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

public class NormalTaskExecutor
        extends TaskExecutor {
    private static Logger s_logger = Logger.getLogger(NormalTaskExecutor.class);
    private static volatile NormalTaskExecutor instance = null;

    public static NormalTaskExecutor getInstance() {
        if (instance == null) {
            synchronized (NormalTaskExecutor.class) {
                if (instance == null) {
                    instance = new NormalTaskExecutor();
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
                    TaskUtil.getInstance();
                    if (TaskUtil.isOkToRun(job.isForceRun(), job.getTask().getTaskName())) {
                        job.getTask().run(job.getStartDate(), job.getEndDate());
                        job.setExecuteStatus(ExecutorJob.ExecuteStatusEnum.SUCCESS.name());
                        job.addExecuteCount();
                        this.done_success += 1L;
                    } else {
                        this.done_success_abandon += 1L;
                        AlertMessage message = new AlertMessage();
                        message.setType(AlertEventType.TaskError.toString());
                        message.setSubject(AlertEventType.TaskError.getContext());
                        message.setContent(new Gson().toJson(job.getTask().getTaskTimeType() + "[NormalTaskExecutor] skip task for out of job list. " + job.toString()));
                        TaskLogUtils.insertToDB(message);
                    }
                } catch (Exception ex) {
                    s_logger.error(this.getClass().getSimpleName() + ".1", ex);
                    job.setExecuteStatus(ExecutorJob.ExecuteStatusEnum.RETRY.name());
                    job.addExecuteCount();
                    RetryTaskExecutor.getInstance().submitJob(job);
                }
                Thread.sleep(200L);
            }
        } catch (Exception ex) {
            TaskLogUtils.recordErrorInfo(ex, job);
        }
    }
}
