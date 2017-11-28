package com.eInsight.task.common;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class TaskTemplate {
    @Expose
    private String taskName;
    @Expose
    private TimeUnit taskTimeType;
    @Expose
    private long taskoffset = 0L;
    private ValueMappingCallBack valueCallBack = new ValueMappingCallBack() {
        public String callback(List<String> countBy) {
            return (countBy != null) && (countBy.size() > 0) ? countBy.get(0) : null;
        }

        public List<String> reverseCallback(String callbackValue) {
            List<String> list = new ArrayList();
            list.add(callbackValue);
            return list;
        }
    };

    public TaskTemplate(String taskName, TimeUnit taskTimeType) {
        this.taskName = taskName;
        this.taskTimeType = taskTimeType;
    }

    public TaskTemplate(String taskName, TimeUnit taskTimeType, long taskoffset) {
        this.taskName = taskName;
        this.taskTimeType = taskTimeType;
        this.taskoffset = taskoffset;
    }

    public TaskTemplate(String taskName, TimeUnit taskTimeType, ValueMappingCallBack valueCallBack, long taskoffset) {
        this.taskName = taskName;
        this.taskTimeType = taskTimeType;
        this.valueCallBack = valueCallBack;
        this.taskoffset = taskoffset;
    }

    public void run(Date startDate, Date endDate) throws Exception {
        try {
            if ((startDate != null) && (endDate != null)) {
                doCountTask(startDate, endDate);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    protected abstract void doCountTask(Date paramDate1, Date paramDate2) throws Exception;

    public String getTaskName() {
        return this.taskName;
    }

    public TimeUnit getTaskTimeType() {
        return this.taskTimeType;
    }

    public long getTaskoffset() {
        return this.taskoffset;
    }

    public ValueMappingCallBack getValueCallBack() {
        return this.valueCallBack;
    }

    public String toString() {
        return "TimeBasedTask [taskName=" + this.taskName + ", taskTimeType=" + this.taskTimeType + "]";
    }
}
