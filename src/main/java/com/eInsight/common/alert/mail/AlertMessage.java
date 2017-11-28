package com.eInsight.common.alert.mail;

import com.eInsight.common.alert.common.entity.AlertEventType;
import com.mongodb.DBObject;

public class AlertMessage {
    private String subject;
    private String type;
    private String content;
    private String status;

    public AlertMessage() {
    }

    public AlertMessage(String subject, AlertEventType type, String content) {
        this.subject = subject;
        this.type = type.name();
        this.content = content;
    }

    public AlertMessage(String subject, AlertEventType type, String content, String status) {
        this.subject = subject;
        this.type = type.name();
        this.content = content;
        this.status = status;
    }

    public AlertMessage(DBObject document) {
        this.subject = String.valueOf(document.get("Subject"));
        this.type = String.valueOf(document.get("Type"));
        this.content = String.valueOf(document.get("Content"));
        this.status = String.valueOf(document.get("Status"));
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public enum AlertStatusEnum {
        AbNormalAlerted("alerted"), ReturnToNormal("Return To Normal");

        private String context;

        public String getContext() {
            return this.context;
        }

        AlertStatusEnum(String context) {
            this.context = context;
        }
    }
}
