package com.eInsight.task.mongoCountTask;

import com.eInsight.common.Initializer;
import com.eInsight.task.common.TaskTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

public class MongoTemplateTaskMapping {
    private static final Logger LOG = Logger.getLogger(MongoTemplateTaskMapping.class);
    private static HashMap<String, String[]> taskName2queryCondition = new HashMap();
    private static List<String> taskNameList = new ArrayList();

    static {
        init();
    }

    public static void init() {
        List<String> tasknames = new ArrayList();
        HashMap<String, String[]> taskname2query = new HashMap();
        try {
            for (TaskTemplate task : Initializer.getTasks()) {
                if (!tasknames.contains(task.getTaskName())) {
                    tasknames.add(task.getTaskName());
                }
                if ((task instanceof MongoTemplateCountByPathTask)) {
                    MongoTemplateCountByPathTask countbypathTask = (MongoTemplateCountByPathTask) task;
                    String[] countByPath = countbypathTask.getCountByPath();
                    if ((countByPath != null) && (countByPath.length > 0)) {
                        taskname2query.put(countbypathTask.getTaskName(), countByPath);
                    }
                } else {
                    taskname2query.put(task.getTaskName(), null);
                }
            }
            taskNameList = tasknames;
            taskName2queryCondition = taskname2query;
        } catch (Exception e) {
            LOG.error("init", e);
        }
    }

    public static String[] getCountByPath(String taskName) {
        return (String[]) taskName2queryCondition.get(taskName);
    }

    public static List<String> getAllTaskNames() {
        return taskNameList;
    }
}
