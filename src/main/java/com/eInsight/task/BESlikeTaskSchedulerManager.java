package com.eInsight.task;

import com.eInsight.task.common.HeartBeatRunnableTask;
import com.eInsight.task.common.TimeUnit;
import com.eInsight.task.executors.AbandonTaskExecutor;
import com.eInsight.task.executors.NormalTaskExecutor;
import com.eInsight.task.executors.RetryTaskExecutor;
import com.eInsight.task.jobCheckTask.LeaderTask;
import com.eInsight.task.jobCheckTask.WorkerTask;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

@Service
public class BESlikeTaskSchedulerManager {
    @Autowired
    @Qualifier("heartBeatTaskScheduler")
    TaskScheduler scheduler;

    @PostConstruct
    public void schedule() {
        new Thread() {
            public void run() {
                NormalTaskExecutor.getInstance().execute();
            }
        }.start();
        new Thread() {
            public void run() {
                RetryTaskExecutor.getInstance().execute();
            }
        }.start();
        new Thread() {
            public void run() {
                AbandonTaskExecutor.getInstance().execute();
            }
        }.start();
        this.scheduler.schedule(new WorkerTask(), new CronTrigger("0 * * * * *"));
        this.scheduler.schedule(new LeaderTask(), new CronTrigger("30 * * * * *"));
        this.scheduler.schedule(new HeartBeatRunnableTask(TimeUnit.MINUTELY), new CronTrigger("5 * * * * *"));
        this.scheduler.schedule(new HeartBeatRunnableTask(TimeUnit.HOURLY), new CronTrigger("0 1 * * * *"));
        this.scheduler.schedule(new HeartBeatRunnableTask(TimeUnit.DAILY), new CronTrigger("0 5 0 * * *"));
    }
}
