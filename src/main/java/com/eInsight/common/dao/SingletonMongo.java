package com.eInsight.common.dao;

import com.eInsight.task.common.TaskLogUtils;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

public class SingletonMongo {
    private static final String MONGO_ADDRESS = "mongodb_address";
    private static final String MONDO_DB = "mongo_db";
    public Mongo mongo;
    private String mongoAddress;
    private String mongodb;
    private static volatile SingletonMongo instance = null;
    private MongoClientOptions.Builder mongoOptionsBuilder;

    private SingletonMongo() {
        initParamFromProperties();
        try {
            this.mongo = initSingleMongo();
            System.out.println("Mongo Initialized!");
        } catch (Exception e) {
            TaskLogUtils.recordErrorInfo(e);
        }
    }

    private Mongo initSingleMongo() throws Exception {
        String[] ipAndPort = this.mongoAddress.split(":");
        MongoClientOptions option = this.mongoOptionsBuilder.build();
        Mongo result = new MongoClient(new ServerAddress(ipAndPort[0], Integer.parseInt(ipAndPort[1])), option);
        return result;
    }

    private void initParamFromProperties() {
        this.mongoAddress = MongoDBFactory.getProperty(MONGO_ADDRESS);
        this.mongodb = MongoDBFactory.getProperty(MONDO_DB);

        this.mongoOptionsBuilder = new MongoClientOptions.Builder()
                .connectionsPerHost(1000)
                .socketKeepAlive(true)
                .connectTimeout(15000)
                .maxWaitTime(5000)
                .socketTimeout(10000)
                .threadsAllowedToBlockForConnectionMultiplier(5000);
    }

    public static SingletonMongo getMongoInstance() {
        if (instance == null) {
            synchronized (SingletonMongo.class) {
                if (instance == null) {
                    instance = new SingletonMongo();
                }
            }
        }
        return instance;
    }

    public Mongo getMongo() {
        return this.mongo;
    }

    public String getMongodb() {
        return this.mongodb;
    }
}
