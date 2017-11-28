package com.eInsight.task.jobCheckTask;

import com.eInsight.common.Initializer;
import com.eInsight.common.dao.impl.ConfigDAOImpl;
import com.eInsight.common.utils.EmptyUtil;
import com.eInsight.resources.ConfigService;
import com.eInsight.task.common.TaskTemplate;
import com.eInsight.task.common.TaskUtil;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

public class LeaderTask implements Runnable {
    private static Logger s_logger = Logger.getLogger(LeaderTask.class);
    private ConfigService configSvc = ConfigService.getInstance();
    public static final long EXPIRED_DURATION = 5 * 60 * 1000L;

    public void run() {
        if (!TaskUtil.isTaskEnabled()) {
            return;
        }
        int randomSleepTime = new Random().nextInt(1000);
        try {
            Thread.sleep(randomSleepTime);
            String thisWorkerIP = TaskUtil.getIP();

            boolean isLeader = false;
            isLeader = competeForLeader(thisWorkerIP);
            if (isLeader) {
                manageWorkers();
            }
            TaskUtil.threadSleepForAWhile();
        } catch (Exception ex) {
            s_logger.error("", ex);
        }
    }

    private void manageWorkers() {
        List<DBObject> allWorkers = this.configSvc.getAllWorkers();
        assignTasks(allWorkers);
        alertInvalidWorkers(allWorkers);
    }

    private void assignTasks(List<DBObject> allWorkers) {
        List<TaskTemplate> taskList = new ArrayList();
        taskList.addAll(Initializer.getTasks());
        taskList.addAll(Initializer.getBackEndTasks());
        Map<String, Object> assignedTaskMap = new HashMap();

        List<DBObject> workersList = getValidWorkers(allWorkers);
        int workerCount = workersList.size();
        if (workerCount == 0) {
            s_logger.info("No Valid Worker!");
            return;
        }
        s_logger.info("all_workers" + new Gson().toJson(allWorkers));

        int currentIndex = -1;
        DBObject currentValidWorker = null;
        for (TaskTemplate task : taskList) {
            String taskName = task.getTaskName();
            if (!assignedTaskMap.containsKey(taskName)) {
                assignedTaskMap.put(taskName, taskName);

                currentIndex = (currentIndex + 1) % workerCount;
                currentValidWorker = workersList.get(currentIndex);
                String assignedTaskOfThisWorker = String.valueOf(currentValidWorker.get("tasklist"));
                assignedTaskOfThisWorker = assignedTaskOfThisWorker + "," + taskName;
                currentValidWorker.put("tasklist", assignedTaskOfThisWorker);
            }
        }
        if (currentIndex > -1) {
            for (DBObject validWorker : workersList) {
                DBObject findObj = new BasicDBObject();
                findObj.put("ip", validWorker.get("ip"));
                findObj.put("param_name", "worker");
                ConfigDAOImpl.getConfigDAOInstace().updateMajority(findObj, validWorker, false);
                s_logger.info("updated_valid_worker" + new Gson().toJson(validWorker));
            }
        }
    }

    private boolean competeForLeader(String workerIP) {
        boolean isLeader = false;
        DBObject findLeader = new BasicDBObject();
        DBObject newLeader = new BasicDBObject();
        DBObject expiredTimeGap = new BasicDBObject("$lt", Long.valueOf(TaskUtil.getExpiredTimeStamp()));
        List<DBObject> orList = new ArrayList();
        orList.add(new BasicDBObject("ip", workerIP));
        orList.add(new BasicDBObject("timestamp", expiredTimeGap));
        findLeader.put("param_name", "leader");
        findLeader.put("$or", orList);
        newLeader.put("ip", workerIP);
        newLeader.put("timestamp", Long.valueOf(System.currentTimeMillis()));
        DBObject updateLeaderObj = new BasicDBObject("$set", newLeader);
        s_logger.info("update_leader" + new Gson().toJson(updateLeaderObj));

        int updateCount = ConfigDAOImpl.getConfigDAOInstace().updateMajority(findLeader, updateLeaderObj, false);
        s_logger.info("updated_leader_count" + String.valueOf(updateCount));
        if (updateCount > 0) {
            isLeader = true;
        }
        return isLeader;
    }

    private void alertInvalidWorkers(List<DBObject> allWorkers) {
        List<DBObject> invalidWorkers = getInvalidWorkers(allWorkers);
        if ((invalidWorkers == null) || (invalidWorkers.size() == 0)) {
            return;
        }
        for (DBObject worker : invalidWorkers) {
            if (!EmptyUtil.isNullOrEmpty(String.valueOf(worker.get("tasklist")))) {
                this.configSvc.alertFailureTask(worker);
            }
            ConfigDAOImpl.getConfigDAOInstace().remove(worker);
            s_logger.info("remove_inValidWorker=" + new Gson().toJson(worker));
        }
    }

    private List<DBObject> getValidWorkers(List<DBObject> allWorkers) {
        List<DBObject> validWorkers = new ArrayList();
        for (DBObject config : allWorkers) {
            if ((config.get("timestamp") != null) && (TaskUtil.isValidTimeStamp(Long.parseLong(String.valueOf(config.get("timestamp")))))) {
                config.put("tasklist", "");
                validWorkers.add(config);
            }
        }
        return validWorkers;
    }

    private List<DBObject> getInvalidWorkers(List<DBObject> allWorkers) {
        List<DBObject> inValidWorkers = new ArrayList();
        for (DBObject config : allWorkers) {
            if ((config.get("timestamp") == null) || (!TaskUtil.isValidTimeStamp(Long.parseLong(String.valueOf(config.get("timestamp")))))) {
                inValidWorkers.add(config);
            }
        }
        return inValidWorkers;
    }
}
