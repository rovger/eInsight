package com.eInsight.common.dao;

import com.eInsight.common.utils.EmptyUtil;
import com.eInsight.task.common.TaskLogUtils;
import com.mongodb.DB;
import com.mongodb.DBCollection;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class MongoDBFactory {
    private static InputStream inputStream = null;
    private static Properties properties = null;

    static {
        try {
            inputStream = new FileInputStream(MongoDBFactory.class.getResource("/mongodb.properties").getPath());
            properties = new Properties();
            properties.load(inputStream);
        } catch (Exception e) {
            TaskLogUtils.recordErrorInfo(e);
        }
    }

    public static DBCollection getCollection(String collection) {
        String dbName = SingletonMongo.getMongoInstance().getMongodb();
        return getDB(dbName).getCollection(collection);
    }

    public static DB getDB(String dbName) {
        return SingletonMongo.getMongoInstance().getMongo().getDB(dbName);
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getReverseProperty(String value) {
        String result = "";
        Set<Map.Entry<Object, Object>> propertiesEntry = properties.entrySet();
        for (Iterator<Map.Entry<Object, Object>> iterator = propertiesEntry.iterator(); iterator.hasNext(); ) {
            Map.Entry<Object, Object> entry = iterator.next();
            String keyInEntry = (String) entry.getKey();
            String valueInEntry = (String) entry.getValue();
            if ((!EmptyUtil.isNullOrEmpty(valueInEntry)) && (!EmptyUtil.isNullOrEmpty(keyInEntry)) && (valueInEntry.equals(value))) {
                result = keyInEntry;
                break;
            }
        }
        return result;
    }
}
