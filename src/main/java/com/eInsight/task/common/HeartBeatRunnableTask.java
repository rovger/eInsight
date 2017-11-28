package com.eInsight.task.common;

import com.eInsight.common.Initializer;
import com.eInsight.task.executors.ExecutorJob;
import com.eInsight.task.executors.NormalTaskExecutor;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

public class HeartBeatRunnableTask implements Runnable {
    private TimeUnit heartBeatTimeType;

    public HeartBeatRunnableTask(TimeUnit taskTimeType) {
        this.heartBeatTimeType = taskTimeType;
    }

    public void run() {
        Date date = new Date();
        date = DateUtils.addMinutes(date, 0);

        Date startDate = null;
        Date endDate = null;
        if (this.heartBeatTimeType == TimeUnit.MINUTELY) {
            date = DateUtils.setMilliseconds(date, 0);
            date = DateUtils.setSeconds(date, 0);
            startDate = DateUtils.addMinutes(date, -1);
            endDate = date;
        } else if (this.heartBeatTimeType == TimeUnit.HOURLY) {
            date = DateUtils.setMilliseconds(date, 0);
            date = DateUtils.setSeconds(date, 0);
            date = DateUtils.setMinutes(date, 0);
            startDate = DateUtils.addHours(date, -1);
            endDate = date;
        } else if (this.heartBeatTimeType == TimeUnit.DAILY) {
            date = DateUtils.setMilliseconds(date, 0);
            date = DateUtils.setSeconds(date, 0);
            date = DateUtils.setMinutes(date, 0);
            date = DateUtils.setHours(date, 0);
            startDate = DateUtils.addHours(date, -24);
            endDate = date;
        } else if (this.heartBeatTimeType == TimeUnit.WEEKLY) {
            Calendar c = Calendar.getInstance();
            c.setFirstDayOfWeek(Calendar.MONDAY);
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            date = c.getTime();
            date = DateUtils.setMilliseconds(date, 0);
            date = DateUtils.setSeconds(date, 0);
            date = DateUtils.setMinutes(date, 0);
            date = DateUtils.setHours(date, 0);
            startDate = DateUtils.addWeeks(date, -1);
            endDate = date;
        } else if (this.heartBeatTimeType == TimeUnit.MONTHLY) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_MONTH, 1);
            date = c.getTime();
            date = DateUtils.setMilliseconds(date, 0);
            date = DateUtils.setSeconds(date, 0);
            date = DateUtils.setMinutes(date, 0);
            date = DateUtils.setHours(date, 0);
            startDate = DateUtils.addMonths(date, -1);
            endDate = date;
        }
        for (TaskTemplate task : Initializer.getTasks()) {
            if (task.getTaskTimeType() == this.heartBeatTimeType) {
                NormalTaskExecutor.getInstance().submitJob(new ExecutorJob(task, startDate, endDate));
            }
        }
        for (TaskTemplate task : Initializer.getBackEndTasks()) {
            if (task.getTaskTimeType() == this.heartBeatTimeType) {
                NormalTaskExecutor.getInstance().submitJob(new ExecutorJob(task, startDate, endDate));
            }
        }
    }
}
