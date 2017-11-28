package com.eInsight.common.dao.impl;

import com.eInsight.common.Initializer;
import com.eInsight.common.dao.ConsoleDAOImpl;
import com.eInsight.common.dao.MongoDBFactory;
import com.mongodb.DBCollection;

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
