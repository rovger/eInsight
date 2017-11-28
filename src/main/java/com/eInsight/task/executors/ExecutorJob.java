package com.eInsight.task.executors;

import com.eInsight.task.common.TaskTemplate;

import java.util.Date;

public class ExecutorJob {
    private TaskTemplate task;
    private Date startDate;
    private Date endDate;
    private String ExecuteStatus;
    private int executeCount = 0;
    private boolean forceRun = false;

    public ExecutorJob(TaskTemplate task, Date startDate, Date endDate) {
        this.task = task;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setForseRun(boolean isforceRun) {
        this.forceRun = isforceRun;
    }

    public boolean isForceRun() {
        return this.forceRun;
    }

    public TaskTemplate getTask() {
        return this.task;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public String getExecuteStatus() {
        return this.ExecuteStatus;
    }

    public void setExecuteStatus(String executeStatus) {
        this.ExecuteStatus = executeStatus;
    }

    public int getExecuteCount() {
        return this.executeCount;
    }

    public void addExecuteCount() {
        this.executeCount += 1;
    }

    public enum ExecuteStatusEnum {
        SUCCESS, RETRY, ABANDON;

        ExecuteStatusEnum() {
        }
    }

    public String toString() {
        return "ExecutorJob [task=" + this.task + ", startDate=" + this.startDate + ", endDate=" + this.endDate + ", ExecuteStatus=" + this.ExecuteStatus + ", executeCount=" + this.executeCount + "]";
    }
}
