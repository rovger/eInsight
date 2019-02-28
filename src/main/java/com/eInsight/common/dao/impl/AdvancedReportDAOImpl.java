package com.eInsight.common.dao.impl;

import com.eInsight.common.Initializer;
import com.eInsight.common.dao.ConsoleDAOImpl;
import com.eInsight.common.dao.MongoDBFactory;
import com.mongodb.DBCollection;

/**
 {
 "_id" : ObjectId("5971790f33781b6abeba8bd3"),
 "TimeMatrix" : "HOUR",
 "Count" : NumberLong(52),
 "EndDate" : ISODate("2017-07-15T10:00:00.000+0000"),
 "ColumnValue" : "SETUP_APM_C2C",
 "StartDate" : ISODate("2017-07-15T09:00:00.000+0000"),
 "CreationDate" : ISODate("2017-07-21T03:46:23.353+0000"),
 "ColumnName" : "PAS_FlowName"
 }
 {
 "_id" : ObjectId("5971790f33781b6abeba8bd4"),
 "TimeMatrix" : "HOUR",
 "Count" : NumberLong(52),
 "EndDate" : ISODate("2017-07-15T10:00:00.000+0000"),
 "ColumnValue" : "SETUP_APM_B2C",
 "StartDate" : ISODate("2017-07-15T09:00:00.000+0000"),
 "CreationDate" : ISODate("2017-07-21T03:46:23.353+0000"),
 "ColumnName" : "PAS_FlowName"
 }
 */
public class AdvancedReportDAOImpl extends ConsoleDAOImpl {
    private static final String CONFIG_COLLECTIONNAME = Initializer.DOMAIN + "_AdvancedReport";
    private DBCollection configDAO = null;
    private static volatile AdvancedReportDAOImpl instace = null;
    private AdvancedReportDAOImpl() {}

    public static AdvancedReportDAOImpl getAdvancedReportDAOInstace() {
        if (instace == null) {
            synchronized (AdvancedReportDAOImpl.class) {
                if (instace == null) {
                    instace = new AdvancedReportDAOImpl();
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
