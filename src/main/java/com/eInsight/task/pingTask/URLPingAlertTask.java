package com.eInsight.task.pingTask;

import com.eInsight.common.alert.common.entity.AlertEventType;
import com.eInsight.common.alert.mail.AlertMessage;
import com.eInsight.task.common.TaskLogUtils;
import com.eInsight.task.common.TaskTemplate;
import com.eInsight.task.common.TimeUnit;
import com.google.gson.Gson;

import java.util.Date;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

public class URLPingAlertTask extends TaskTemplate {
    private String pingURL;

    public URLPingAlertTask(String taskName, TimeUnit taskTimeType, String url) {
        super(taskName, taskTimeType);
        this.pingURL = url;
    }

    protected void doCountTask(Date startDate, Date endDate)
            throws Exception {
        HttpClient client = new HttpClient();
        GetMethod getMethod = new GetMethod(this.pingURL);
        client.executeMethod(getMethod);
        int status = getMethod.getStatusCode();
        if (status / 100 != 2) {
            HashMap<String, Object> alertContentMap = new HashMap();
            alertContentMap.put("pingURL", this.pingURL);
            alertContentMap.put("httpstatus", Integer.valueOf(status));
            alertContentMap.put("httpresponse", getMethod.getResponseBodyAsString());

            AlertMessage m = new AlertMessage("Target URL cannot reached", AlertEventType.URLPing, new Gson().toJson(alertContentMap));
            TaskLogUtils.insertToDB(m);
        }
    }
}
