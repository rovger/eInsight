package com.eInsight.common.dao.impl;

import com.eInsight.common.Initializer;
import com.eInsight.common.dao.ConsoleDAOImpl;
import com.eInsight.common.dao.MongoDBFactory;
import com.mongodb.DBCollection;

/**
 {
 "_id" : ObjectId("59716aca33781b6abeba837a"),
 "CreationDate" : ISODate("2017-07-21T02:45:30.357+0000"),
 "Content" : "{\"_id\":{\"_time\":1500605100,\"_machine\":-1050195944,\"_inc\":268854595,\"_new\":false},\"ip\":\"10.249.66.204\",\"param_name\":\"worker\"}",
 "Subject" : "worker experience down time",
 "Type" : "WorkerDown"
 }
 {
 "_id" : ObjectId("59716aed33781b6abeba837b"),
 "CreationDate" : ISODate("2017-07-21T02:46:05.012+0000"),
 "Content" : "\"MINUTELY[NormalTaskExecutor] skip task for out of job list. ExecutorJob [task\\u003dMongoTemplateCountByPathTask [countByPath\\u003d[Ljava.lang.String;@645ad2b2, toString()\\u003dMongoTemplateTask [mongoTemplateName\\u003dcountByPath, toString()\\u003dTimeBasedTask [taskName\\u003dPAS_FlowName, taskTimeType\\u003dMINUTELY]]], startDate\\u003dFri Jul 21 10:45:00 CST 2017, endDate\\u003dFri Jul 21 10:46:00 CST 2017, ExecuteStatus\\u003dnull, executeCount\\u003d0]\"",
 "Subject" : "Task execute error",
 "Type" : "TaskError"
 }
 {
 "_id" : ObjectId("598c1e0d4af3752dd46da694"),
 "CreationDate" : ISODate("2017-08-10T08:49:16.950+0000"),
 "Content" : "Alert Rule<br/>If  PAS_FlowName:SETUP_APM_C2C:MINUTELY:< 1 , then alert.<br/><br/>The Current date is 2017-08-10 16:48:00 - 2017-08-10 16:49:00<br/><br/>the current count is abnormal. <br/><br/> current count is 0<br/> threshod count is 1<br/><a href='http://localhost:8080/views/reporting?reportName=PAS_FlowName'> click me to open  easyinsight reporting  </a>!<br/><div style='text-align:center; color:green'>============================= Don't worried, For Demo Testing! =============================</div>",
 "Subject" : "ReportCountAlertTask::PAS_FlowName::SETUP_APM_C2C",
 "Type" : "ReportAlert"
 }
 */
public class AlertDAOImpl extends ConsoleDAOImpl {
    private static final String CONFIG_COLLECTIONNAME = Initializer.DOMAIN + "_Alert";
    private DBCollection configDAO = null;
    private static volatile AlertDAOImpl instace = null;
    private AlertDAOImpl() {}

    public static AlertDAOImpl getAlertDAOInstace() {
        if (instace == null) {
            synchronized (AlertDAOImpl.class) {
                if (instace == null) {
                    instace = new AlertDAOImpl();
                }
            }
        }
        return instace;
    }

    public DBCollection getDAOCollection() {
        if (this.configDAO == null) {
            synchronized (this) {
                if (this.configDAO == null) {
                    this.configDAO = MongoDBFactory.getCollection(CONFIG_COLLECTIONNAME);
                }
            }
        }
        return this.configDAO;
    }
}
