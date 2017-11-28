package com.eInsight.task.common;

import com.eInsight.common.Initializer;
import com.eInsight.resources.ConfigService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.eInsight.task.jobCheckTask.LeaderTask;
import org.apache.log4j.Logger;

public class TaskUtil {
    private static Logger s_logger = Logger.getLogger(TaskUtil.class);
    private static volatile TaskUtil instance = null;
    private static List<String> myJobList = new ArrayList();
    private static volatile boolean isEnabled = true;

    private TaskUtil() {
        setJobList();
    }

    public static TaskUtil getInstance() {
        if (instance == null) {
            synchronized (TaskUtil.class) {
                if (instance == null) {
                    instance = new TaskUtil();
                }
            }
        }
        return instance;
    }

    public static void setJobList() {
        ConfigService configSvc = ConfigService.getInstance();
        try {
            myJobList = configSvc.getJobList();
        } catch (Exception ex) {
            s_logger.error("setJobList error", ex);
        }
    }

    public static List<String> getJobList() {
        return myJobList;
    }

    public static boolean isMyJob(String taskName) {
        return myJobList.contains(taskName);
    }

    public static boolean isOkToRun(boolean isJobForceRun, String taskName) {
        boolean isMyJob = isMyJob(taskName);
        boolean isTaskRunable = isTaskEnabled();
        return ((isMyJob) || (isJobForceRun)) && (isTaskRunable);
    }

    public static void disableWorker() {
        isEnabled = false;
        myJobList = new ArrayList();
    }

    public static void enableWorker() {
        isEnabled = true;
    }

    public static boolean isWorkerEnabled() {
        return isEnabled;
    }

    public static boolean isTaskEnabled() {
        return (isEnabled) && (Initializer.isInitialized);
    }

    public static String getIP() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    public static void threadSleepForAWhile() {
        try {
            Thread.sleep(200L);
        } catch (Exception e) {
            s_logger.error("threadSleepForAWhile", e);
        }
    }

    public static long getExpiredTimeStamp() {
        return System.currentTimeMillis() - LeaderTask.EXPIRED_DURATION;
    }

    public static boolean isValidTimeStamp(long timestamp) {
        if (getExpiredTimeStamp() < timestamp) {
            return true;
        }
        return false;
    }
}
