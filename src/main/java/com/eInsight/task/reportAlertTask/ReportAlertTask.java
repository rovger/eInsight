package com.eInsight.task.reportAlertTask;

import com.eInsight.common.alert.common.entity.AlertEventType;
import com.eInsight.common.alert.mail.AlertMessage;
import com.eInsight.common.dao.ConsoleDAOImpl;
import com.eInsight.common.dao.impl.AdvancedReportDAOImpl;
import com.eInsight.common.dao.impl.ReportDAOImpl;
import com.eInsight.common.utils.EmptyUtil;
import com.eInsight.task.common.TaskLogUtils;
import com.eInsight.task.common.TaskTemplate;
import com.eInsight.task.common.TimeUnit;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

public class ReportAlertTask extends TaskTemplate {
    private ReportAlertCallBack alertCallback = new ReportAlertCallBack() {
        public String alert(ReportAlertTask reportAlertTask) {
            return null;
        }

        public boolean isAbnormalData(ReportAlertTask reportAlertTask) {
            BigDecimal count = new BigDecimal(reportAlertTask.getCount(ReportAlertTask.this.reportData));
            if (count.compareTo(new BigDecimal(reportAlertTask.thresholdValue)) <= 0) {
                return true;
            }
            return false;
        }
    };
    private ConsoleDAOImpl fromDAO;
    private String reportName;
    private String reportType;
    private String thresholdValue;
    private boolean isAbnormal;
    protected Date startDate;
    protected Date endDate;
    protected Map<String, String> reportData = new HashMap();
    protected int xDaysToAlert;

    public ReportAlertTask(String taskName, TimeUnit taskTimeType, String reportName, ReportAlertCallBack alertCallback) {
        super(taskName, taskTimeType);
        this.alertCallback = alertCallback;
        this.reportName = reportName;
    }

    public ReportAlertTask(String taskName, TimeUnit taskTimeType, String reportName, String reportType, String thresholdValue, ReportAlertCallBack alertCallback) {
        super(taskName, taskTimeType);
        this.alertCallback = alertCallback;
        this.reportName = reportName;
        this.thresholdValue = thresholdValue;
        this.reportType = reportType;
    }

    public ReportAlertTask(String taskName, TimeUnit taskTimeType, String reportName, String reportType, ReportAlertCallBack alertCallback) {
        super(taskName, taskTimeType);
        this.reportName = reportName;
        this.reportType = reportType;
        this.alertCallback = alertCallback;
    }

    protected void doCountTask(Date startDate, Date endDate)
            throws Exception {
        if (this.xDaysToAlert != 0) {
            this.startDate = DateUtils.addDays(startDate, -this.xDaysToAlert);
            startDate = DateUtils.addDays(startDate, -this.xDaysToAlert);
        } else {
            this.startDate = startDate;
        }
        this.endDate = endDate;

        String timeMatrixToQuery = TimeUnit.MINUTELY.getName();
        TimeUnit taskTimeType = getTaskTimeType();
        if ((taskTimeType == TimeUnit.MINUTELY) || (taskTimeType == TimeUnit.HOURLY)) {
            this.fromDAO = ReportDAOImpl.getReportDAOInstace();
        } else {
            timeMatrixToQuery = TimeUnit.HOURLY.getName();
            this.fromDAO = AdvancedReportDAOImpl.getAdvancedReportDAOInstace();
        }
        this.reportData = getReportData(this.reportName, this.reportType, timeMatrixToQuery, startDate, endDate);
        this.isAbnormal = this.alertCallback.isAbnormalData(this);
        String alertMessage = this.alertCallback.alert(this);
        AlertMessage message = new AlertMessage(getTaskName(), AlertEventType.ReportAlert, alertMessage);
        if (this.isAbnormal) {
            TaskLogUtils.insertToDB(message);
        }
        doExtraAlert(message, this.reportData);
    }

    protected Map getReportData(String reportName, String reportType, String timeMatrixToQuery, Date startDate, Date endDate) {
        Map<String, String> reportValueMap = new HashMap();
        BasicDBObject con = new BasicDBObject();
        con.put("ColumnName", reportName);
        if (EmptyUtil.isNullOrEmpty(reportType)) {
            con.put("ColumnValue", reportType);
        }
        con.put("TimeMatrix", timeMatrixToQuery);
        BasicDBObject timegap = new BasicDBObject();
        timegap.put("$gte", startDate);
        timegap.put("$lt", endDate);
        con.put("StartDate", timegap);
        List<DBObject> reportData = this.fromDAO.queryListByObject(con);
        for (DBObject obj : reportData) {
            String reportValue = String.valueOf(obj.get("ColumnValue"));
            Object countObj = obj.get("Count");
            BigDecimal count = new BigDecimal(String.valueOf(countObj == null ? "0" : countObj));
            Object reportValueObj = reportValueMap.get(reportValue);
            if (reportValueObj != null) {
                count = count.add(new BigDecimal(reportValueObj.toString()));
            }
            reportValueMap.put(reportValue, count.toString());
        }
        return reportValueMap;
    }

    public String getCount() {
        return getCount(this.reportData);
    }

    protected String getCount(Map<String, String> reportData) {
        BigDecimal count = new BigDecimal("0");
        if (EmptyUtil.isNullOrEmpty(this.reportType)) {
            Object reportCountObj = reportData.get(this.reportType);
            if (reportCountObj != null) {
                count = new BigDecimal(reportCountObj.toString());
            }
        } else {
            for (String reportVaule : reportData.keySet()) {
                Object reportCountObj = reportData.get(reportVaule);
                if (reportCountObj != null) {
                    count = count.add(new BigDecimal(reportCountObj.toString()));
                }
            }
        }
        return count.toString();
    }

    protected String getTotalCount(Map<String, String> reportData) {
        BigDecimal total = new BigDecimal("0");
        for (String reportValue : reportData.keySet()) {
            Object reportCountObj = reportData.get(reportValue);
            if (reportCountObj != null) {
                total = total.add(new BigDecimal(reportCountObj.toString()));
            }
        }
        return total.toString();
    }

    protected void doExtraAlert(AlertMessage alertMessage, Map<String, String> reportData) {
    }

    public ReportAlertCallBack getAlertCallback() {
        return this.alertCallback;
    }

    public ConsoleDAOImpl getFromDAO() {
        return this.fromDAO;
    }

    public String getReportName() {
        return this.reportName;
    }

    public String getReportType() {
        return this.reportType;
    }

    public String getThresholdValue() {
        return this.thresholdValue;
    }

    public boolean isAbnormal() {
        return this.isAbnormal;
    }

    public Map<String, String> getReportData() {
        return this.reportData;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public int getxDaysToAlert() {
        return this.xDaysToAlert;
    }

    public void setxDaysToAlert(int xDaysToAlert) {
        this.xDaysToAlert = xDaysToAlert;
    }
}
