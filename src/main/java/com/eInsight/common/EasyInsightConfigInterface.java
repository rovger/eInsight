package com.eInsight.common;

import com.eInsight.task.common.TaskTemplate;

import java.util.List;
import java.util.Map;

public interface EasyInsightConfigInterface {
    Map<String, String> getCoreConfig();

    List<TaskTemplate> getTasks();

    List<TaskTemplate> getBackEndTasks();
}