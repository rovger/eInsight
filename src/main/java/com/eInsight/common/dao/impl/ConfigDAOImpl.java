package com.eInsight.common.dao.impl;

import com.eInsight.common.Initializer;
import com.eInsight.common.dao.ConsoleDAOImpl;
import com.eInsight.common.dao.MongoDBFactory;
import com.eInsight.task.common.TaskLogUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 {
 "_id" : ObjectId("59716ab9c16748181006654a"),
 "ip" : "10.249.66.204",
 "param_name" : "leader",
 "timestamp" : NumberLong(1502357070526)
 }
 {
 "_id" : ObjectId("598bfcf052bb9a1f41781fd7"),
 "ip" : "10.249.66.204",
 "param_name" : "worker",
 "timestamp" : NumberLong(1502357100024),
 "tasklist" : ",PAS_FlowName,PAS_operationName,APMSetupAllFlow,APMSetupSubFlow_C2C,APMSetupSubFlow_B2C,ReportCountAlertTask::PAS_FlowName::SETUP_APM_C2C,ReportPercentAlertTask::APMSetupAllFlow::APMSetupFlow_Initialized,pulldatatask"
 }
 */
public class ConfigDAOImpl extends ConsoleDAOImpl {
    private static final String CONFIG_COLLECTIONNAME = Initializer.DOMAIN + "_Config";
    private DBCollection configDAO = null;
    private static volatile ConfigDAOImpl instace = null;
    private ConfigDAOImpl() {}

    public static ConfigDAOImpl getConfigDAOInstace() {
        if (instace == null) {
            synchronized (ConfigDAOImpl.class) {
                if (instace == null) {
                    instace = new ConfigDAOImpl();
                    initIndex(instace);
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

    private static void initIndex(ConfigDAOImpl configDAO) {
        DBObject background = new BasicDBObject("background", Boolean.valueOf(true));
        try {
            configDAO.getDAOCollection().createIndex(new BasicDBObject("param_name", Integer.valueOf(1)), background);
            configDAO.getDAOCollection().createIndex(new BasicDBObject("ip", Integer.valueOf(1)), background);
        } catch (Exception e) {
            TaskLogUtils.recordErrorInfo(e);
        }
    }
}
