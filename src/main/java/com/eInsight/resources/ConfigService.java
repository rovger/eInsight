package com.eInsight.resources;

import com.eInsight.common.EasyInsightConfigInterface;
import com.eInsight.common.Initializer;
import com.eInsight.common.alert.common.entity.AlertEventType;
import com.eInsight.common.alert.mail.AlertMessage;
import com.eInsight.common.dao.impl.ConfigDAOImpl;
import com.eInsight.common.utils.EmptyUtil;
import com.eInsight.task.common.TaskLogUtils;
import com.eInsight.task.common.TaskTemplate;
import com.eInsight.task.common.TaskUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import groovy.lang.GroovyClassLoader;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
@Path("/config")
public class ConfigService {
    private static Logger s_logger = Logger.getLogger(ConfigService.class);
    private static volatile ConfigService instance = null;
    @Context
    HttpServletRequest servletRequest;

    public static ConfigService getInstance() {
        if (instance == null) {
            synchronized (ConfigService.class) {
                if (instance == null) {
                    instance = new ConfigService();
                }
            }
        }
        return instance;
    }

    @GET
    @Path("/allworkers")
    public String allWorkers() {
        return new Gson().toJson(getAllWorkers());
    }

    @GET
    @Path("/competeforleader")
    public String competeForLeader() {
        try {
            DBObject query = new BasicDBObject();
            query.put("param_name", "leader");
            List<DBObject> configs = ConfigDAOImpl.getConfigDAOInstace().queryListByObjectWithLimit(query, 1);
            DBObject leader;
            if (configs.size() == 0) {
                DBObject findleader = new BasicDBObject();
                findleader.put("param_name", "leader");
                String ip = TaskUtil.getIP();
                leader = new BasicDBObject();
                leader.put("ip", ip);
                leader.put("param_name", "leader");
                leader.put("timestamp", Long.valueOf(System.currentTimeMillis()));
                ConfigDAOImpl.getConfigDAOInstace().updateMajority(findleader, leader, true);
                System.out.println("---------- initializer compete for leader at " + new Date().toString());
            } else {
                leader = configs.get(0);
            }
            return new Gson().toJson(leader);
        } catch (Exception e) {
            s_logger.error("competeforleader failure", e);
            return new Gson().toJson(e.getMessage());
        }
    }

    @GET
    @Path("/leader")
    public String getLeader() {
        try {
            DBObject query = new BasicDBObject();
            query.put("param_name", "leader");
            List<DBObject> configs = ConfigDAOImpl.getConfigDAOInstace().queryListByObjectWithLimit(query, 1);
            DBObject leader;
            if (configs.size() != 0) {
                leader = configs.get(0);
            } else {
                leader = new BasicDBObject("leader", "No Leader, Please Compete For Leader!");
            }
            return new Gson().toJson(leader);
        } catch (Exception e) {
            s_logger.error(e.getMessage());
            return new Gson().toJson(e.getMessage());
        }
    }

    @GET
    @Path("/thisworker/check")
    public String checkFailureTask() {
        DBObject response = new BasicDBObject();
        try {
            DBObject config = getWorker(TaskUtil.getIP());
            if (config != null) {
                if ((!EmptyUtil.isNullOrEmpty(String.valueOf(config.get("tasklist")))) &&
                        (config.get("timestamp") != null) && (!TaskUtil.isValidTimeStamp(((Long) config.get("timestamp")).longValue()))) {
                    response.put("message", alertFailureTask(config));
                }
                response.put("worker", config);
            }
        } catch (Exception e) {
            response.put("error", e.getMessage());
            s_logger.error("checkFailureTask", e);
        }
        return new Gson().toJson(response);
    }

    @GET
    @Path("/thisworker/disable")
    public String disableThisWorker() {
        try {
            TaskUtil.disableWorker();
        } catch (Exception e) {
            s_logger.error(e.getMessage());
            return new Gson().toJson(new BasicDBObject("error", e.getMessage()));
        }
        return "This Worker Status :" + (TaskUtil.isWorkerEnabled() ? "enabled" : "disabled") + "!";
    }

    @GET
    @Path("/thisworker/enable")
    public String enableThisWorker() {
        try {
            TaskUtil.enableWorker();
        } catch (Exception e) {
            s_logger.error(e.getMessage());
            return new Gson().toJson(new BasicDBObject("error", e.getMessage()));
        }
        return "This Worker Status :" + (TaskUtil.isWorkerEnabled() ? "enabled" : "disabled") + "!";
    }

    @GET
    @Path("/initializerconfig")
    public String getInitializerconfig() {
        try {
            String response = getTaskConfig();
            return response == null ? "No Config Exists!" : response;
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        }
    }

    @GET
    @Path("/initializerconfig/{index}")
    public String getConfigHistryByIndex(@PathParam("index") String index) {
        try {
            BasicDBObject reportingconfig = new BasicDBObject();
            reportingconfig.put("param_name", "InitializerConfig");
            int front = Integer.valueOf(index).intValue();
            List<DBObject> configs = ConfigDAOImpl.getConfigDAOInstace().getDAOCollection().find(reportingconfig).sort(new BasicDBObject("CreationDate", Integer.valueOf(-1))).limit(1).skip(front).toArray();
            if ((configs != null) && (configs.size() > 0)) {
                return (configs.get(0)).get("param_value").toString();
            }
        } catch (Exception ex) {
            s_logger.error(ex.getMessage());
            return new Gson().toJson(new BasicDBObject("error", ex.getMessage()));
        }
        return "index illegal, please try again.";
    }

    @POST
    @Path("/initializerconfig")
    public String initializerconfig(String taskConfig) {
        BasicDBObject reportingconfig = new BasicDBObject();
        try {
            if (new Initializer().loadConfig(taskConfig)) {
                reportingconfig.put("param_name", "InitializerConfig");
                reportingconfig.put("param_value", taskConfig);
                reportingconfig.put("CreationDate", new Date());
                ConfigDAOImpl.getConfigDAOInstace().save(reportingconfig);
                TaskUtil.threadSleepForAWhile();
            } else {
                throw new RuntimeException("initializerconfig error, could not load config!");
            }
            return new Gson().toJson(reportingconfig);
        } catch (Exception e) {
            s_logger.error("initializerconfig failure", e);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        }
    }

    @GET
    @Path("/allTasks")
    public String loadAllTasks() {
        List<TaskTemplate> resultTaskList = new ArrayList();
        List<String> checkList = new ArrayList();
        Map<String, List<TaskTemplate>> taskMap = new HashMap();
        try {
            List<TaskTemplate> taskList = Initializer.getTasks();
            if ((taskList != null) && (taskList.size() > 0)) {
                for (TaskTemplate task : taskList) {
                    if (!checkList.contains(task.getTaskName())) {
                        resultTaskList.add(task);
                        checkList.add(task.getTaskName());
                    }
                }
            }
        } catch (Exception ex) {
            s_logger.error("loadAllTasks failure", ex);
        }
        taskMap.put("taskInfo", resultTaskList);
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(taskMap);
    }

    public String getTaskConfig() {
        DBObject reportingconfig = new BasicDBObject();
        reportingconfig.put("param_name", "InitializerConfig");
        List<DBObject> configs = ConfigDAOImpl.getConfigDAOInstace().queryListByObjectWithLimitAndSort(reportingconfig, 1, new Object[]{"CreationDate", Integer.valueOf(-1)});
        if ((configs != null) && (configs.size() > 0)) {
            reportingconfig = configs.get(0);
            return reportingconfig.get("param_value").toString();
        }
        return null;
    }

    public EasyInsightConfigInterface loadConfig(String groovyString) throws Exception {
        EasyInsightConfigInterface config = null;
        if (!EmptyUtil.isNullOrEmpty(groovyString)) {
            ClassLoader cl = Initializer.class.getClassLoader();
            GroovyClassLoader groovyCl = new GroovyClassLoader(cl);
            Class groovyClass = groovyCl.parseClass(groovyString);
            config = (EasyInsightConfigInterface) groovyClass.newInstance();
        }
        return config;
    }

    public List<String> getJobList() throws UnknownHostException {
        DBObject mytaskConfig = getWorker(TaskUtil.getIP());
        if ((mytaskConfig != null) && (mytaskConfig.get("tasklist") != null)) {
            String tasknames = String.valueOf(mytaskConfig.get("tasklist"));
            String[] tasksArr = tasknames.split(",");
            return Arrays.asList(tasksArr);
        }
        return new ArrayList();
    }

    public List<DBObject> getAllWorkers() {
        DBObject query = new BasicDBObject();
        query.put("param_name", "worker");
        return ConfigDAOImpl.getConfigDAOInstace().queryListByObject(query);
    }

    public DBObject getWorker(String ip) {
        DBObject query = new BasicDBObject();
        query.put("param_name", "worker");
        query.put("ip", ip);
        List<DBObject> configs = ConfigDAOImpl.getConfigDAOInstace().queryListByObjectWithLimit(query, 1);
        DBObject config = (configs == null) || (configs.size() == 0) ? null : configs.get(0);
        return config;
    }

    public AlertMessage alertFailureTask(DBObject worker) {
        AlertMessage message = new AlertMessage();
        message.setType(AlertEventType.WorkerDown.toString());
        message.setSubject(AlertEventType.WorkerDown.getContext());
        message.setContent(new Gson().toJson(worker));
        TaskLogUtils.insertToDB(message);
        return message;
    }
}
