package com.eInsight.common.dao.impl;

import com.eInsight.common.Initializer;
import com.eInsight.common.dao.ConsoleDAOImpl;
import com.eInsight.common.dao.MongoDBFactory;
import com.mongodb.DBCollection;

public class RawDataDAOImpl extends ConsoleDAOImpl {
    private static final String CONFIG_COLLECTIONNAME = Initializer.EVENT_COLLECTIONNAME;
    private DBCollection rawDataDAO = null;
    private static volatile RawDataDAOImpl instance = null;
    private RawDataDAOImpl() {}

    public static RawDataDAOImpl getRawDataDAOInstance() {
        if (instance == null) {
            synchronized (RawDataDAOImpl.class) {
                if (instance == null) {
                    instance = new RawDataDAOImpl();
                }
            }
        }
        return instance;
    }

    public DBCollection getDAOCollection() {
        if (this.rawDataDAO == null) {
            synchronized (this) {
                if (this.rawDataDAO == null) {
                    this.rawDataDAO = MongoDBFactory.getCollection(CONFIG_COLLECTIONNAME);
                }
            }
        }
        return this.rawDataDAO;
    }
}
