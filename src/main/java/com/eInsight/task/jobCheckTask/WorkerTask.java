package com.eInsight.task.jobCheckTask;

import com.eInsight.common.dao.impl.ConfigDAOImpl;
import com.eInsight.task.common.TaskUtil;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.log4j.Logger;

public class WorkerTask implements Runnable {
    private static Logger s_logger = Logger.getLogger(WorkerTask.class);

    public void run() {
        try {
            String thisWorkerIP = TaskUtil.getIP();
            if (!TaskUtil.isTaskEnabled()) {
                registerWorker(thisWorkerIP);
                return;
            }
            updateTimeStamp(thisWorkerIP);

            TaskUtil.setJobList();
            s_logger.info("this_worker_job_list " + new Gson().toJson(TaskUtil.getJobList()));
        } catch (Exception ex) {
            s_logger.error("", ex);
        }
    }

    private void registerWorker(String workerIP) {
        DBObject findWorker = new BasicDBObject();
        DBObject updateWorker = new BasicDBObject();
        findWorker.put("ip", workerIP);
        findWorker.put("param_name", "worker");

        updateWorker.put("ip", workerIP);
        updateWorker.put("param_name", "worker");
        DBObject updateObj = new BasicDBObject("$set", findWorker);
        s_logger.info("registerWorker" + new Gson().toJson(updateWorker));

        int updateCount = ConfigDAOImpl.getConfigDAOInstace().updateMajority(findWorker, updateObj, true);
        s_logger.info("registered_worker_count" + String.valueOf(updateCount));
        if (updateCount == 0) {
            throw new RuntimeException("register worker failure, ip: " + workerIP);
        }
    }

    private void updateTimeStamp(String workerIP) {
        DBObject findWorker = new BasicDBObject();
        DBObject updateWorker = new BasicDBObject();
        findWorker.put("ip", workerIP);
        findWorker.put("param_name", "worker");
        updateWorker.put("ip", workerIP);
        updateWorker.put("param_name", "worker");
        updateWorker.put("timestamp", Long.valueOf(System.currentTimeMillis()));
        DBObject updateObj = new BasicDBObject("$set", updateWorker);
        int updateCount = ConfigDAOImpl.getConfigDAOInstace().updateMajority(findWorker, updateObj, true);
        if (updateCount == 0) {
            throw new RuntimeException("update worker timestamp failure, ip: " + workerIP);
        }
    }
}
