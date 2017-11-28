package com.eInsight.task.mongoCountTask;

import com.eInsight.task.common.TimeUnit;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MongoTemplateCountByPathTask extends MongoTemplateTask {
    private String[] countByPath;

    public MongoTemplateCountByPathTask(String taskName, TimeUnit taskTimeType, String... countByPath) {
        super(taskName, taskTimeType, getTemplateName());
        this.countByPath = countByPath;
    }

    public MongoTemplateCountByPathTask(String taskName, TimeUnit taskTimeType, long taskoffset, String... countByPath) {
        super(taskName, taskTimeType, getTemplateName(), taskoffset);
        this.countByPath = countByPath;
    }

    protected void customizeDataMap(Map<String, Object> dataMap) {
        if (this.countByPath == null) {
            return;
        }
        List<String> pathList = Arrays.asList(this.countByPath);
        dataMap.put("pathList", pathList);

        dataMap.put("matchCondition", "CreationDate");
    }

    private static String getTemplateName() {
        return "countByPath";
    }

    public String[] getCountByPath() {
        return this.countByPath;
    }

    public String toString() {
        return "MongoTemplateCountByPathTask [countByPath=" + this.countByPath + ", toString()=" + super.toString() + "]";
    }
}
