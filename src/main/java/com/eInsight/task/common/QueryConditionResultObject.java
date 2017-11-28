package com.eInsight.task.common;

import com.mongodb.DBObject;

public class QueryConditionResultObject {
    private DBObject matchObject;
    private DBObject projectObject;
    private DBObject groupObject;

    public QueryConditionResultObject(DBObject matchObject, DBObject projectObject, DBObject groupObject) {
        this.matchObject = matchObject;
        this.projectObject = projectObject;
        this.groupObject = groupObject;
    }

    public DBObject getMatchObject() {
        return this.matchObject;
    }

    public void setMatchObject(DBObject matchObject) {
        this.matchObject = matchObject;
    }

    public DBObject getProjectObject() {
        return this.projectObject;
    }

    public void setProjectObject(DBObject projectObject) {
        this.projectObject = projectObject;
    }

    public DBObject getGroupObject() {
        return this.groupObject;
    }

    public void setGroupObject(DBObject groupObject) {
        this.groupObject = groupObject;
    }
}
