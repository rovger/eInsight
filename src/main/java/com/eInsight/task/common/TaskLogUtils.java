package com.eInsight.task.common;

import com.eInsight.common.alert.common.entity.AlertEventType;
import com.eInsight.common.alert.mail.AlertMessage;
import com.eInsight.common.dao.impl.AlertDAOImpl;
import com.eInsight.common.utils.EmptyUtil;
import com.eInsight.task.executors.ExecutorJob;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.apache.log4j.Logger;

public class TaskLogUtils {
    private static Logger s_logger = Logger.getLogger(TaskLogUtils.class);

    public static void recordErrorInfo(Exception ex) {
        try {
            BasicDBObject document = new BasicDBObject();
            StringWriter strWriter = new StringWriter();
            ex.printStackTrace(new PrintWriter(strWriter));
            document.append("CreationDate", new Date())
                    .append("Subject", ex.getMessage())
                    .append("Content", strWriter.toString())
                    .append("Type", AlertEventType.TaskError.name());
            AlertDAOImpl.getAlertDAOInstace().save(document);
        } catch (Exception e) {
            s_logger.error("recordErrorInfo", e);
        }
    }

    public static void recordErrorInfo(Exception e, ExecutorJob job) {
        try {
            BasicDBObject document = new BasicDBObject();
            StringWriter strWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(strWriter));
            document.append("CreationDate", new Date())
                    .append("Subject", job.getTask().getTaskName())
                    .append("Content", job.toString() + "\n" + strWriter.toString())
                    .append("Type", AlertEventType.TaskError.name());
            AlertDAOImpl.getAlertDAOInstace().save(document);
        } catch (Exception ex) {
            s_logger.error("recordErrorInfo", ex);
        }
    }

    public static void insertToDB(AlertMessage m) {
        BasicDBObject document = new BasicDBObject();
        document.append("CreationDate", new Date())
                .append("Subject", m.getSubject())
                .append("Content", m.getContent())
                .append("Type", m.getType());
        if (!EmptyUtil.isNullOrEmpty(m.getStatus())) {
            document.append("Status", m.getStatus());
        }
        AlertDAOImpl.getAlertDAOInstace().save(document);
    }

    public static void updateOrInsertToDB(DBObject document) {
        AlertDAOImpl.getAlertDAOInstace().save(document);
    }
}
