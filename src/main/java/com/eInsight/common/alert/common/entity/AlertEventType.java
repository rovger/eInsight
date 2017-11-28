package com.eInsight.common.alert.common.entity;

public enum AlertEventType {
    TaskError("Task execute error"),
    URLPing("URL Ping failed"),
    ReportAlert("report alert"),
    Alert_EMAIL("email"),
    WorkerDown("worker experience down time");

    private String context;

    public String getContext() {
        return this.context;
    }

    AlertEventType(String context) {
        this.context = context;
    }
}