package com.eInsight.task.reportAlertTask;

public abstract class ReportAlertCallBack {
    public String alertRule = "";
    public String alertType = "";
    public String alertThreshold = "";

    public String alert(ReportAlertTask reportAlertTask) {
        return null;
    }

    public boolean isAbnormalData(ReportAlertTask reportAlertTask) {
        return false;
    }

    public ReportAlertCallBack(String alertRule, String alertType, String alertThreshold) {
        setAlertRule(alertRule);
        setAlertType(alertType);
        setAlertThreshold(alertThreshold);
    }

    public ReportAlertCallBack() {
    }

    public String getAlertRule() {
        return this.alertRule;
    }

    public String getAlertType() {
        return this.alertType;
    }

    public String getAlertThreshold() {
        return this.alertThreshold;
    }

    public void setAlertRule(String str) {
        this.alertRule = str;
    }

    public void setAlertType(String str) {
        this.alertType = str;
    }

    public void setAlertThreshold(String str) {
        this.alertThreshold = str;
    }

    public void init() {
    }
}
