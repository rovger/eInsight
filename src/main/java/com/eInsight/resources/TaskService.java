package com.eInsight.resources;

import com.eInsight.common.Initializer;
import com.eInsight.task.common.TaskTemplate;
import com.eInsight.task.common.TimeUnit;
import com.eInsight.task.executors.AbandonTaskExecutor;
import com.eInsight.task.executors.ExecutorJob;
import com.eInsight.task.executors.NormalTaskExecutor;
import com.eInsight.task.executors.RetryTaskExecutor;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
@Path("/task")
public class TaskService {
    private static final Logger s_logger = Logger.getLogger(TaskService.class);
    @Context
    HttpServletRequest servletRequest;

    @GET
    @Path("/queue")
    @Produces({"application/json"})
    public String getqueue() {
        HashMap result = new HashMap();
        result.put("NormalTaskExecutor", listSnapshot(NormalTaskExecutor.getInstance().getTasks()));
        result.put("RetryTaskExecutor", listSnapshot(RetryTaskExecutor.getInstance().getTasks()));
        result.put("AbandonTaskExecutor", listSnapshot(AbandonTaskExecutor.getInstance().getTasks()));
        return new Gson().toJson(result);
    }

    @GET
    @Path("/queuehistory")
    @Produces({"application/json"})
    public String getqueueHistory() {
        HashMap result = new HashMap();
        result.put("NormalTaskExecutor.getTaskDoneSuccess", Long.valueOf(NormalTaskExecutor.getInstance().getTaskDoneSuccess()));
        result.put("RetryTaskExecutor.getTaskDoneSuccess", Long.valueOf(RetryTaskExecutor.getInstance().getTaskDoneSuccess()));
        result.put("AbandonTaskExecutor.getTaskDoneSuccess", Long.valueOf(AbandonTaskExecutor.getInstance().getTaskDoneSuccess()));

        result.put("NormalTaskExecutor.getTaskDoneSuccessAbandon", Long.valueOf(NormalTaskExecutor.getInstance().getTaskDoneSuccessAbandon()));
        result.put("RetryTaskExecutor.getTaskDoneSuccessAbandon", Long.valueOf(RetryTaskExecutor.getInstance().getTaskDoneSuccessAbandon()));
        result.put("AbandonTaskExecutor.getTaskDoneSuccessAbandon", Long.valueOf(AbandonTaskExecutor.getInstance().getTaskDoneSuccessAbandon()));

        result.put("NormalTaskExecutor.getTaskSumitted", Long.valueOf(NormalTaskExecutor.getInstance().getTaskSumitted()));
        result.put("RetryTaskExecutor.getTaskSumitted", Long.valueOf(RetryTaskExecutor.getInstance().getTaskSumitted()));
        result.put("AbandonTaskExecutor.getTaskSumitted", Long.valueOf(AbandonTaskExecutor.getInstance().getTaskSumitted()));
        return new Gson().toJson(result);
    }

    @GET
    @Path("/task-type")
    @Produces({"application/json"})
    public String gettaskdefinition() {
        HashMap result = new HashMap();
        result.put("Initializer", listSnapshot(Initializer.getTasks()));
        return new Gson().toJson(result);
    }

    @GET
    @Path("/execute/{taskName}/{datematrix}/{startDate}/{endDate}")
    @Produces({"application/json"})
    public String runBatchTask(
            @PathParam("taskName") String taskName,
            @PathParam("datematrix") String datematrix,
            @PathParam("startDate") String startDateStr,
            @PathParam("endDate") String endDateStr) {
        HashMap result = new HashMap();
        result.put("matchedTask", "");
        try {
            List<TaskTemplate> tasklist = Initializer.getTasks();
            tasklist.addAll(Initializer.getBackEndTasks());
            for (TaskTemplate task : tasklist) {
                if ((task.getTaskName().equals(taskName)) &&
                        (datematrix.equals(task.getTaskTimeType().getName().toLowerCase()))) {
                    result.put("matchedTask", task.toString());
                    result.put("taskList", new ArrayList());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd&HH:mm");
                    Date dateend = format.parse(endDateStr);
                    Date date = format.parse(startDateStr);
                    Date startDate = null;
                    Date endDate = null;
                    while (dateend.getTime() > date.getTime()) {
                        if (task.getTaskTimeType() == TimeUnit.MINUTELY) {
                            date = DateUtils.setMilliseconds(date, 0);
                            date = DateUtils.setSeconds(date, 0);
                            startDate = date;
                            endDate = DateUtils.addMinutes(startDate, 1);
                        } else if (task.getTaskTimeType() == TimeUnit.HOURLY) {
                            date = DateUtils.setMilliseconds(date, 0);
                            date = DateUtils.setSeconds(date, 0);
                            date = DateUtils.setMinutes(date, 0);
                            startDate = date;
                            endDate = DateUtils.addHours(startDate, 1);
                        } else if (task.getTaskTimeType() == TimeUnit.DAILY) {
                            date = DateUtils.setMilliseconds(date, 0);
                            date = DateUtils.setSeconds(date, 0);
                            date = DateUtils.setMinutes(date, 0);
                            date = DateUtils.setHours(date, 0);
                            startDate = date;
                            endDate = DateUtils.addHours(startDate, 24);
                        }
                        List taskList = (List) result.get("taskList");
                        taskList.add(format.format(startDate) + "--" + format.format(endDate));
                        try {
                            ExecutorJob job = new ExecutorJob(task, startDate, endDate);
                            job.setForseRun(true);
                            NormalTaskExecutor.getInstance().submitJob(job);
                            date = endDate;
                        } catch (Exception e) {
                            result.put("error", e.getCause().getMessage());
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            s_logger.error("runBatchTask", e);
            return new Gson().toJson(new BasicDBObject("error", e.getMessage()));
        }
        return new Gson().toJson(result);
    }

    private HashMap listSnapshot(List list) {
        HashMap result = new HashMap();
        result.put("size", Integer.valueOf(list.size()));
        List<String> strlist = new ArrayList();
        for (Object obj : list) {
            strlist.add(obj.toString());
        }
        result.put("list", strlist);
        return result;
    }
}
