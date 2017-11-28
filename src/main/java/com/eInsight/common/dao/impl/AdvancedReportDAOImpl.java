package com.eInsight.common.dao.impl;

import com.eInsight.common.Initializer;
import com.eInsight.common.dao.ConsoleDAOImpl;
import com.eInsight.common.dao.MongoDBFactory;
import com.mongodb.DBCollection;

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
