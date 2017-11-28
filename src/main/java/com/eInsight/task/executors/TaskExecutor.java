package com.eInsight.task.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.log4j.Logger;

public abstract class TaskExecutor {
    private static Logger s_logger = Logger.getLogger(TaskExecutor.class);
    protected int MAX_TASKS = Integer.MAX_VALUE;
    protected LinkedBlockingDeque<ExecutorJob> taskQueue;
    protected long taskSumitted = 0L;
    protected long taskTake = 0L;
    protected long done_success = 0L;
    protected long done_success_abandon = 0L;
    protected int subBatch = 1;

    protected TaskExecutor() {
        this.taskQueue = new LinkedBlockingDeque(this.MAX_TASKS);
    }

    public abstract void execute();

    public void submitJob(ExecutorJob job) {
        try {
            this.taskQueue.put(job);
            this.taskSumitted += 1L;
        } catch (Exception ex) {
            s_logger.error("submitJob", ex);
        }
    }

    public List<ExecutorJob> getTasks() {
        List<ExecutorJob> currentTasks = new ArrayList();
        Object[] taskObjList = this.taskQueue.toArray();
        if (taskObjList != null) {
            for (Object taskObj : taskObjList) {
                currentTasks.add((ExecutorJob) taskObj);
            }
        }
        return currentTasks;
    }

    public long getTaskSumitted() {
        return this.taskSumitted;
    }

    public long getTaskTake() {
        return this.taskTake;
    }

    public long getTaskDoneSuccess() {
        return this.done_success;
    }

    public long getTaskDoneSuccessAbandon() {
        return this.done_success_abandon;
    }

    public void setSubBatch(int subBatch) {
        this.subBatch = subBatch;
    }
}
