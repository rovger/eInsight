package com.eInsight.common.dao.impl;

import com.eInsight.common.Initializer;
import com.eInsight.common.dao.ConsoleDAOImpl;
import com.eInsight.common.dao.MongoDBFactory;
import com.mongodb.DBCollection;

/**
 {
 "_id" : ObjectId("5971750d33781b6abeba8aac"),
 "TimeMatrix" : "MINUTE",
 "Count" : NumberLong(20),
 "EndDate" : ISODate("2017-07-14T16:39:00.000+0000"),
 "ColumnValue" : "SETUP_APM_C2C",
 "StartDate" : ISODate("2017-07-14T16:38:00.000+0000"),
 "CreationDate" : ISODate("2017-07-21T03:29:17.588+0000"),
 "ColumnName" : "PAS_FlowName"
 }
 {
 "_id" : ObjectId("597175b833781b6abeba8ac6"),
 "TimeMatrix" : "MINUTE",
 "Count" : NumberLong(10),
 "EndDate" : ISODate("2017-07-15T06:40:00.000+0000"),
 "ColumnValue" : "SETUP_APM_B2C",
 "StartDate" : ISODate("2017-07-15T06:39:00.000+0000"),
 "CreationDate" : ISODate("2017-07-21T03:32:08.929+0000"),
 "ColumnName" : "PAS_FlowName"
 }
 */
public class ReportDAOImpl extends ConsoleDAOImpl {
    private static final String CONFIG_COLLECTIONNAME = Initializer.DOMAIN + "_Report";
    private DBCollection configDAO = null;
    private static volatile ReportDAOImpl instace = null;
    private ReportDAOImpl() {}

    public static ReportDAOImpl getReportDAOInstace() {
        if (instace == null) {
            synchronized (ReportDAOImpl.class) {
                if (instace == null) {
                    instace = new ReportDAOImpl();
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
