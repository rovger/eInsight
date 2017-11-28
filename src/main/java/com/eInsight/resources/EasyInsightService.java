package com.eInsight.resources;

import com.eInsight.common.Initializer;
import com.eInsight.task.common.TaskTemplate;
import com.eInsight.task.reportAlertTask.ReportEmailAlertTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
@Path("/easyinsight")
public class EasyInsightService {
    @Context
    HttpServletRequest servletRequest;

    @GET
    @Path("/easyConfig")
    public String easyConfig() {
        BasicDBObject response = new BasicDBObject();
        try {
            HashMap<String, String> coreConfig = Initializer.getCoreConfigs();
            List<TaskTemplate> backEndTaskList = Initializer.getBackEndTasks();
            List<TaskTemplate> frontendTaskList = Initializer.getTasks();
            ArrayList<Object> backEndTaskCompiled = new ArrayList();
            List<String> reportNameList = new LinkedList();
            for (TaskTemplate task : backEndTaskList) {
                HashMap<String, Object> taskCompiled = new HashMap();
                String taskName = task.getTaskName();
                String taskTimeType = task.getTaskTimeType().toString();
                long taskOffset = task.getTaskoffset();
                taskCompiled.put("TaskName", taskName);
                taskCompiled.put("TaskTimeType", taskTimeType);
                if (taskOffset != 0L) {
                    taskCompiled.put("TaskOffset", Long.valueOf(taskOffset));
                }
                if ((task instanceof ReportEmailAlertTask)) {
                    ReportEmailAlertTask taskCasted = (ReportEmailAlertTask) task;
                    taskCasted.getAlertCallback().init();
                    String reportName = taskCasted.getReportName();
                    String alertRule = taskCasted.getAlertCallback().getAlertRule();
                    String alertType = taskCasted.getAlertCallback().getAlertType();
                    String alertThreshold = taskCasted.getAlertCallback().getAlertThreshold();
                    String toEmail = taskCasted.getToEmail();
                    String fromEmail = taskCasted.getFromEmal();
                    reportNameList.add(reportName);
                    taskCompiled.put("ReportName", reportName);
                    taskCompiled.put("AlertType", alertType);
                    taskCompiled.put("AlertRule", alertRule);
                    taskCompiled.put("AlertThreshold", alertThreshold);
                    taskCompiled.put("EmailTo", toEmail);
                    taskCompiled.put("EmailFrom", fromEmail);
                }
                backEndTaskCompiled.add(taskCompiled);
            }
            response.put("CoreConfig", coreConfig);

            response.put("BackEndTask", backEndTaskCompiled);
            response.put("FrontEndTask", frontendTaskList);
            response.put("result", "success");
        } catch (Exception e) {
            response.put("result", "failure");
            response.put("message", e.getMessage());
        }
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String result = gson.toJson(response);
        return result;
    }

    @GET
    @Path("/apis/{baseurl}")
    @Produces({"application/json"})
    public String getEventsByTimeOffset(@PathParam("baseurl") String baseurl) {
        List<BasicDBObject> apiList = new ArrayList();

        List<String> configList = new ArrayList();
        String baseapipath = "http://" + baseurl + "/eInsight/template";
        configList.add("Add Tasks In Groovy Style, Method:Post");
        configList.add(baseapipath + "/config/initializerconfig");
        configList.add("Get Tasks, Method:Get");
        configList.add(baseapipath + "/config/initializerconfig");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd&HH:mm");
        Date date = new Date();
        date = DateUtils.setMilliseconds(date, 0);
        date = DateUtils.setSeconds(date, 0);
        date = DateUtils.setMinutes(date, 0);
        Date startDate = DateUtils.addHours(date, -1);
        Date endDate = date;
        configList.add("Do Last 1 hour task Manually, Method:Get");
        configList.add(baseapipath + "/task/execute/<taskName>/minute/" + format.format(startDate) + "/" + format.format(endDate));
        startDate = DateUtils.addHours(date, -12);
        configList.add("Do Last 12 hours task Manually, Method:Get");
        configList.add(baseapipath + "/task/execute/<taskName>/minute/" + format.format(startDate) + "/" + format.format(endDate));
        startDate = DateUtils.addHours(date, -24);
        configList.add("Do Last 24 hours task Manually, Method:Get");
        configList.add(baseapipath + "/task/execute/<taskName>/minute/" + format.format(startDate) + "/" + format.format(endDate));
        configList.add("Get Task Queue, Method:Get");
        configList.add(baseapipath + "/task/queue");
        configList.add("Get Task Executed History, Method:Get");
        configList.add(baseapipath + "/task/queuehistory");
        apiList.add(new BasicDBObject("Task Service", configList));

        List<String> reportList = new ArrayList();
        reportList.add("Report Query, Method:Get");
        reportList.add(baseapipath + "/report/events/<columnname>/<columnvalue>/<limit>");
        apiList.add(new BasicDBObject("Report Service", reportList));

        List<String> eventList = new ArrayList();
        eventList.add("Query Raw Data By Path And Value, Method:Get");
        eventList.add(baseapipath + "/event/<mongopath>/<mongovalue>/<limit>");
        eventList.add("Find By Condition, Method:Get");
        eventList.add(baseapipath + "/event/find/<condition>/<limit>");
        apiList.add(new BasicDBObject("Raw Data Service", eventList));

        List<String> adminlist = new ArrayList();
        adminlist.add("Get All Workers Working For This Domian, Method:Get");
        adminlist.add(baseapipath + "/config/allworkers");
        adminlist.add("Get The Leader Of This Domain, Method:Get");
        adminlist.add(baseapipath + "/config/leader");
        apiList.add(new BasicDBObject("Admin", adminlist));

        return new Gson().toJson(apiList);
    }
}
