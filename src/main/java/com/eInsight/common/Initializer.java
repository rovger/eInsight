package com.eInsight.common;

import com.eInsight.common.utils.EmptyUtil;
import com.eInsight.resources.ConfigService;
import com.eInsight.task.common.TaskTemplate;
import com.eInsight.task.mongoCountTask.MongoTemplateTaskMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

public class Initializer extends BaseInitializer {
    private static Logger s_logger = Logger.getLogger(Initializer.class);
    public static String DOMAIN = "eInsight";
    public static String EVENT_COLLECTIONNAME = "eInsightEvent";
    private static volatile EasyInsightConfigInterface config = null;
    public static volatile boolean isInitialized = false;
    private static ConfigService configSvc = ConfigService.getInstance();

    public void init() {
        try {
            loadConfig();
        } catch (Exception ex) {
            s_logger.error("init error: ", ex);
        }
    }

    private boolean loadConfig() {
        return loadConfig(configSvc.getTaskConfig());
    }

    public boolean loadConfig(String configString) {
        boolean isloadSuccess = false;
        try {
            config = configSvc.loadConfig(configString);
            if (config != null) {
                EVENT_COLLECTIONNAME = config.getCoreConfig().get("EVENT_COLLECTIONNAME");
                if (!EmptyUtil.isNullOrEmpty(EVENT_COLLECTIONNAME)) {
                    if (!isInitialized) {
                        super.init();
                        isInitialized = true;
                    }
                    MongoTemplateTaskMapping.init();
                    isloadSuccess = true;
                } else {
                    throw new RuntimeException("event colleciton name is a must!");
                }
            }
        } catch (Exception ex) {
            s_logger.error("loadConfig", ex);
            throw new RuntimeException(ex);
        }
        return (isloadSuccess) && (isInitialized);
    }

    public static List<TaskTemplate> getTasks() {
        List<TaskTemplate> tasks = new ArrayList();
        if ((config != null) && (config.getTasks() != null) && (config.getTasks().size() > 0)) {
            tasks.addAll(config.getTasks());
        }
        return tasks;
    }

    public static List<TaskTemplate> getBackEndTasks() {
        List<TaskTemplate> backendTasks = new ArrayList();
        if ((config != null) && (config.getBackEndTasks() != null) && (config.getBackEndTasks().size() > 0)) {
            backendTasks.addAll(config.getBackEndTasks());
        }
        return backendTasks;
    }

    public static HashMap<String, String> getCoreConfigs() {
        HashMap<String, String> coreConfigs = new HashMap();
        if ((config != null) && (config.getCoreConfig() != null) && (config.getCoreConfig().size() > 0)) {
            coreConfigs.putAll(config.getCoreConfig());
        }
        return coreConfigs;
    }
}
