package com.eInsight.task.reportAlertTask;

import com.eInsight.common.Initializer;
import com.eInsight.common.alert.common.entity.AlertEventType;
import com.eInsight.common.alert.mail.AlertMail;
import com.eInsight.common.alert.mail.AlertMessage;
import com.eInsight.common.dao.impl.AlertDAOImpl;
import com.eInsight.task.common.TaskLogUtils;
import com.eInsight.task.common.TimeUnit;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class ReportEmailAlertTask extends ReportAlertTask {
    private String fromEmail = "weijlu@ebay.com";
    private String toEmail;
    private static final Logger LOG = Logger.getLogger(ReportEmailAlertTask.class);

    public ReportEmailAlertTask(String taskName, TimeUnit taskTimeType, String reportName, String reportType, ReportAlertCallBack alertCallback, String fromEmail, String toEmail) {
        super(taskName, taskTimeType, reportName, reportType, alertCallback);
        this.fromEmail = fromEmail;
        this.toEmail = toEmail;
    }

    public ReportEmailAlertTask(String taskName, TimeUnit taskTimeType, String reportName, String reportType, String thresholdValue, ReportAlertCallBack alertCallback, String fromEmail, String toEmailList) {
        super(taskName, taskTimeType, reportName, reportType, thresholdValue, alertCallback);
        this.toEmail = toEmailList;
        this.fromEmail = fromEmail;
    }

    public ReportEmailAlertTask(String taskName, TimeUnit taskTimeType, String reportName, ReportAlertCallBack alertCallback, String fromEmail, String toEmailList) {
        super(taskName, taskTimeType, reportName, alertCallback);
        this.toEmail = toEmailList;
        this.fromEmail = fromEmail;
    }

    protected void doExtraAlert(AlertMessage alertMessage, Map<String, String> reportData) {
        try {
            DBObject alertObj = findAlert(getTaskName(), AlertEventType.Alert_EMAIL.name());
            if (isAbnormal()) {
                sendMail(true, alertMessage.getContent(), getCount(reportData));
                alertMessage.setStatus(AlertMessage.AlertStatusEnum.AbNormalAlerted.name());
                alertMessage.setType(AlertEventType.Alert_EMAIL.name());
                TaskLogUtils.insertToDB(alertMessage);
            } else if ((alertObj != null) && (String.valueOf(alertObj.get("Status")).equals(AlertMessage.AlertStatusEnum.AbNormalAlerted.name()))) {
                sendMail(false, alertMessage.getContent(), getCount(reportData));
                alertObj.put("Status", AlertMessage.AlertStatusEnum.ReturnToNormal.name());
                TaskLogUtils.updateOrInsertToDB(alertObj);
            }
        } catch (Exception e) {
            LOG.error("doReportEmailAlertTask_Exception, " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void sendMail(boolean isWarning, String alertContent, String count) throws Exception {
        String subject = "";
        String alertSubjectBackground = "red";

        String warnFlag = "WARN";
        if (!isWarning) {
            warnFlag = "OK";
            alertSubjectBackground = "#84bd00";
        }
        String reportName = getReportName();
        String reportType = getReportType();
        String alertTimeType = getTaskTimeType().toString();
        String xDays = "_" + getxDaysToAlert();
        if (getxDaysToAlert() != 0) {
            alertTimeType = alertTimeType + xDays;
        }
        getAlertCallback().init();
        String alertType = getAlertCallback().getAlertType();
        if ("Count".equals(alertType)) {
            subject = "[" + Initializer.DOMAIN + "::" + warnFlag + "] " + reportName + ":" + reportType + ":" + alertTimeType + " Count = " + count;
        } else if ("Percent".equals(alertType)) {
            String percent = "0";
            BigDecimal total = new BigDecimal(getTotalCount(this.reportData));
            BigDecimal target = new BigDecimal(count);
            if ((total.longValue() != 0L) && (target.longValue() != 0L)) {
                double temp = target.divide(total, 3, 4).doubleValue();
                percent = String.valueOf(temp * 100.0D) + "%";
            }
            subject = "[" + Initializer.DOMAIN + "::" + warnFlag + "] " + reportName + ":" + reportType + ":" + alertTimeType + " Percent = " + percent;
        }
        ReportAlertContentBuilder contentBuilder = new ReportAlertContentBuilder("ReportAlertMail");
        Map<String, Object> dataMap = contentBuilder.getDataMap();
        dataMap.put("subject", subject);
        dataMap.put("alertContent", alertContent);
        dataMap.put("alertSubjectBackground", alertSubjectBackground);
        String mailContent = contentBuilder.buildcontent();
        AlertMail.sendMail(this.fromEmail, this.toEmail, subject, mailContent);
    }

    protected DBObject findAlert(String subject, String type) {
        BasicDBObject con = new BasicDBObject();
        con.put("Subject", subject);
        con.put("Type", type);
        List<DBObject> alertData = AlertDAOImpl.getAlertDAOInstace().queryListByObjectWithLimitAndSort(con, 1, new Object[]{"CreationDate", Integer.valueOf(-1)});
        return alertData.size() > 0 ? alertData.get(0) : null;
    }

    public String getFromEmal() {
        return this.fromEmail;
    }

    public void setFromEmal(String fromEmal) {
        this.fromEmail = fromEmal;
    }

    public String getToEmail() {
        return this.toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }
}
