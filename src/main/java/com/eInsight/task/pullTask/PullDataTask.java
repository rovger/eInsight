package com.eInsight.task.pullTask;

import com.eInsight.common.alert.common.entity.AlertEventType;
import com.eInsight.common.alert.mail.AlertMessage;
import com.eInsight.task.common.TaskLogUtils;
import com.eInsight.task.common.TaskTemplate;
import com.eInsight.task.common.TimeUnit;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class PullDataTask
        extends TaskTemplate {
    private Logger s_logger = Logger.getLogger(PullDataTask.class.getSimpleName());
    private String dataURL;

    public PullDataTask(String taskName, TimeUnit taskTimeType, String dataURL) {
        super(taskName, taskTimeType);
        this.dataURL = dataURL;
    }

    protected void doCountTask(Date startDate, Date endDate)
            throws Exception {
        Date startTime = new Date(startDate.getTime() - super.getTaskoffset());
        Date endTime = new Date(endDate.getTime() - super.getTaskoffset());

        PullDataCallBack pullCallBack = new PullDataCallBack();
        String url = pullCallBack.buildURL(this.dataURL, startTime, endTime, new String[0]);
        this.s_logger.info("request_url" + url);

        Client client = Client.create(new DefaultClientConfig());
        WebResource webResource = client.resource(url);
        ClientResponse response = webResource.get(ClientResponse.class);
        String respStr = IOUtils.toString(response.getEntityInputStream());
        this.s_logger.info("raw response" + respStr);
        if (response.getStatus() / 100 != 2) {
            AlertMessage message = new AlertMessage();
            message.setType(AlertEventType.TaskError.toString());
            message.setSubject(AlertEventType.TaskError.getContext());
            message.setContent(new Gson().toJson("JerseyClient, JerseyClientException, http status: " + response.getStatus()));
            TaskLogUtils.insertToDB(message);
            throw new Exception("http status is not 200");
        }
        List<Object> list = pullCallBack.buildMap(respStr);
        this.s_logger.info("PullDataCallbackToBuildMap" + list.toString());
        for (Object objMap : list) {
            pullCallBack.saveDate(startDate, url, objMap);
        }
    }

    public String toString() {
        return "PullDataTask [dataURL=" + this.dataURL + ", toString()=" + super.toString() + "]";
    }
}
