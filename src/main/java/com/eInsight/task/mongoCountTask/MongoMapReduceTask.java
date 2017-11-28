package com.eInsight.task.mongoCountTask;

import com.eInsight.task.common.TimeUnit;
import com.eInsight.task.common.ValueMappingCallBack;

import java.util.Map;

public class MongoMapReduceTask extends MongoTemplateTask {
    private String mapScript;
    private String reduceScript;

    public MongoMapReduceTask(String taskName, TimeUnit taskTimeType, String mapScript, String reduceScript) {
        super(taskName, taskTimeType, getTemplateName());
        this.mapScript = mapScript;
        this.reduceScript = reduceScript;
    }

    public MongoMapReduceTask(String taskName, TimeUnit taskTimeType, ValueMappingCallBack valueCallBack, Map<String, Object> matchMap, String mapScript, String reduceScript, long taskoffset) {
        super(taskName, taskTimeType, getTemplateName(), valueCallBack, matchMap, taskoffset);
        this.mapScript = mapScript;
        this.reduceScript = reduceScript;
    }

    protected void customizeDataMap(Map<String, Object> dataMap) {
        dataMap.put("mapScript", this.mapScript);
        dataMap.put("reduceScript", this.reduceScript);
        dataMap.put("matchCondition", "CreationDate");
    }

    private static String getTemplateName() {
        return "MapReduce";
    }

    public String toString() {
        return "MongoMapReduceTask [mapScript=" + this.mapScript + ", reduceScript=" + this.reduceScript + ", toString()=" + super.toString() + "]";
    }
}
