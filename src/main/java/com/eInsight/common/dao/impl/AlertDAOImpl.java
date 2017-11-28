package com.eInsight.common.dao.impl;

import com.eInsight.common.Initializer;
import com.eInsight.common.dao.ConsoleDAOImpl;
import com.eInsight.common.dao.MongoDBFactory;
import com.mongodb.DBCollection;

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
