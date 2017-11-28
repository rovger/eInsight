package com.eInsight.common.dao;

import com.eInsight.common.utils.ConvertUtil;
import com.eInsight.common.utils.EmptyUtil;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

public abstract class ConsoleDAOImpl<T> implements MongoDAO<T> {
    private static Logger s_logger = Logger.getLogger(ConsoleDAOImpl.class);

    public void save(T bean) {
        save(bean, false);
    }

    public ObjectId save(T bean, boolean writeConcernSafe) {
        ObjectId result = null;
        DBCollection collection = getDAOCollection();
        DBObject object = ConvertUtil.beanToDBObject(bean);
        if (writeConcernSafe) {
            collection.save(object, WriteConcern.SAFE);
            result = (ObjectId) object.get(ConvertUtil._ID);
        } else {
            collection.save(object);
        }
        return result;
    }

    public void insert(T bean) {
        insert(bean, false);
    }

    public ObjectId insert(T bean, boolean writeConcernSafe) {
        ObjectId result = null;
        DBCollection collection = getDAOCollection();
        DBObject object = ConvertUtil.beanToDBObject(bean);
        if (writeConcernSafe) {
            collection.insert(object, WriteConcern.SAFE);
            result = (ObjectId) object.get(ConvertUtil._ID);
        } else {
            collection.insert(new DBObject[]{object});
        }
        return result;
    }

    public void updateBeanByObjectId(ObjectId objectId, T bean) {
        updateBeanByFieldValue(ConvertUtil._ID, objectId, bean);
    }

    public void updateFieldsByObjectId(ObjectId objectId, Map updateMap) {
        updateFieldsByFieldValue(ConvertUtil._ID, objectId, updateMap);
    }

    public void updateBeanByFieldValue(String queryField, Object queryValue, T bean) {
        DBCollection collection = getDAOCollection();

        DBObject query = new BasicDBObject();
        query.put(queryField, queryValue);

        collection.update(query, ConvertUtil.beanToDBObject(bean));
    }

    public void updateFieldsByFieldValue(String queryField, Object queryValue, Map updateMap) {
        DBCollection collection = getDAOCollection();

        DBObject query = new BasicDBObject();
        query.put(queryField, queryValue);
        if (!EmptyUtil.isNullOrEmpty(updateMap)) {
            DBObject updateDoc = new BasicDBObject();
            updateDoc.putAll(updateMap);

            collection.update(query, updateDoc);
        }
    }

    public void updateBeanByParamMap(Map queryParam, T bean) {
        DBCollection collection = getDAOCollection();

        DBObject query = new BasicDBObject();
        if (!EmptyUtil.isNullOrEmpty(queryParam)) {
            query.putAll(queryParam);
        }
        collection.update(query, ConvertUtil.beanToDBObject(bean));
    }

    public void updateFieldsByParamMap(Map queryParam, Map updateMap) {
        DBCollection collection = getDAOCollection();

        DBObject query = new BasicDBObject();
        if (!EmptyUtil.isNullOrEmpty(queryParam)) {
            query.putAll(queryParam);
        }
        if (!EmptyUtil.isNullOrEmpty(updateMap)) {
            DBObject updateDoc = new BasicDBObject();
            updateDoc.putAll(updateMap);

            collection.update(query, updateDoc);
        }
    }

    public void removeByObjectId(ObjectId objectId) {
        removeByFieldValue(ConvertUtil._ID, objectId);
    }

    public void removeByFieldValue(String field, Object fieldValue) {
        DBCollection collection = getDAOCollection();

        DBObject query = new BasicDBObject();
        query.put(field, fieldValue);

        collection.remove(query);
    }

    public void removeByParamMap(Map queryParam) {
        if (!EmptyUtil.isNullOrEmpty(queryParam)) {
            DBCollection collection = getDAOCollection();

            DBObject query = new BasicDBObject();
            query.putAll(queryParam);

            collection.remove(query);
        }
    }

    public void setOneFieldByObjectId(ObjectId objectId, String setField, Object setValue) {
        setOneFieldByFieldValue(ConvertUtil._ID, objectId, setField, setValue, false, false);
    }

    public void setOneFieldByFieldValue(String queryField, Object queryValue, String setField, Object setValue) {
        setOneFieldByFieldValue(queryField, queryValue, setField, setValue, false, false);
    }

    public void setOneFieldByParamMap(Map queryParam, String setField, Object setValue) {
        setOneFieldByParamMap(queryParam, setField, setValue, false, false);
    }

    public void setOneFieldByFieldValue(String queryField, Object queryValue, String setField, Object setValue, boolean upsert, boolean multi) {
        operateOneFieldByFieldValue(queryField, queryValue, setField, setValue, upsert, multi, "$set");
    }

    public void setOneFieldByParamMap(Map queryParam, String setField, Object setValue, boolean upsert, boolean multi) {
        operateOneFieldByParamMap(queryParam, setField, setValue, upsert, multi, "$set");
    }

    public void setMultiFieldByObjectId(ObjectId objectId, Map setMap) {
        Map<String, ObjectId> queryParam = new HashMap();
        queryParam.put(ConvertUtil._ID, objectId);

        operateMulitFieldByParamMap(queryParam, setMap, false, false, "$set");
    }

    public void setMultiFieldByFieldValue(String queryField, Object queryValue, Map setMap) {
        Map<String, Object> queryParam = new HashMap();
        queryParam.put(queryField, queryValue);

        operateMulitFieldByParamMap(queryParam, setMap, false, false, "$set");
    }

    public void setMultiFieldByParamMap(Map queryParam, Map setMap) {
        operateMulitFieldByParamMap(queryParam, setMap, false, false, "$set");
    }

    public void setMultiFieldByFieldValue(String queryField, Object queryValue, Map setMap, boolean upsert, boolean multi) {
        Map<String, Object> queryParam = new HashMap();
        queryParam.put(queryField, queryValue);

        operateMulitFieldByParamMap(queryParam, setMap, upsert, multi, "$set");
    }

    public void setMultiFieldByParamMap(Map queryParam, Map setMap, boolean upsert, boolean multi) {
        operateMulitFieldByParamMap(queryParam, setMap, upsert, multi, "$set");
    }

    public void increaseOneFieldNumberByObjectId(ObjectId objectId, String increaseField, Number increaseCount) {
        increaseOneFieldNumberByFieldValue(ConvertUtil._ID, objectId, increaseField, increaseCount);
    }

    public void increaseMultiFieldNumberByObjectId(ObjectId objectId, Map increaseMap) {
        Map<String, ObjectId> queryParam = new HashMap();
        queryParam.put(ConvertUtil._ID, objectId);

        operateMulitFieldByParamMap(queryParam, increaseMap, false, false, "$inc");
    }

    public void increaseOneFieldNumberByFieldValue(String queryField, Object queryValue, String increaseField, Number increaseCount) {
        increaseOneFieldNumberByFieldValue(queryField, queryValue, increaseField, increaseCount, false, false);
    }

    public void increaseMultiFieldNumberByFieldValue(String queryField, Object queryValue, Map increaseMap) {
        Map<String, Object> queryParam = new HashMap();
        queryParam.put(queryField, queryValue);

        operateMulitFieldByParamMap(queryParam, increaseMap, false, false, "$inc");
    }

    public void increaseOneFieldNumberByParamMap(Map queryParam, String increaseField, Number increaseCount) {
        increaseOneFieldNumberByParamMap(queryParam, increaseField, increaseCount, false, false);
    }

    public void increaseMultiFieldNumberByParamMap(Map queryParam, Map increaseMap) {
        operateMulitFieldByParamMap(queryParam, increaseMap, false, false, "$inc");
    }

    public void increaseOneFieldNumberByFieldValue(String queryField, Object queryValue, String increaseField, Number increaseCount, boolean upsert, boolean multi) {
        operateOneFieldByFieldValue(queryField, queryValue, increaseField, increaseCount, upsert, multi, "$inc");
    }

    public void increaseMultiFieldNumberByFieldValue(String queryField, Object queryValue, Map increaseMap, boolean upsert, boolean multi) {
        Map<String, Object> queryParam = new HashMap();
        queryParam.put(queryField, queryValue);

        operateMulitFieldByParamMap(queryParam, increaseMap, upsert, multi, "$inc");
    }

    public void increaseOneFieldNumberByParamMap(Map queryParam, String increaseField, Number increaseCount, boolean upsert, boolean multi) {
        operateOneFieldByParamMap(queryParam, increaseField, increaseCount, upsert, multi, "$inc");
    }

    public void increaseMultiFieldNumberByParamMap(Map queryParam, Map increaseMap, boolean upsert, boolean multi) {
        operateMulitFieldByParamMap(queryParam, increaseMap, upsert, multi, "$inc");
    }

    public void operateOneFieldByObjectId(ObjectId objectId, String updateField, Object updateValue, boolean upsert, boolean multi, String operateCmd) {
        operateOneFieldByFieldValue(ConvertUtil._ID, objectId, updateField, updateValue, upsert, multi, operateCmd);
    }

    public void operateMultiFieldByObjectId(ObjectId objectId, Map operateMap, boolean upsert, boolean multi, String operateCmd) {
        Map<String, ObjectId> queryParam = new HashMap();
        queryParam.put(ConvertUtil._ID, objectId);

        operateMulitFieldByParamMap(queryParam, operateMap, upsert, multi, operateCmd);
    }

    public void operateOneFieldByFieldValue(String queryField, Object queryValue, String updateField, Object updateValue, boolean upsert, boolean multi, String operateCmd) {
        Map<String, Object> queryParam = new HashMap();
        queryParam.put(queryField, queryValue);

        operateOneFieldByParamMap(queryParam, updateField, updateValue, upsert, multi, operateCmd);
    }

    public void operateMultiFieldByFieldValue(String queryField, Object queryValue, Map operateMap, boolean upsert, boolean multi, String operateCmd) {
        Map<String, Object> queryParam = new HashMap();
        queryParam.put(queryField, queryValue);

        operateMulitFieldByParamMap(queryParam, operateMap, upsert, multi, operateCmd);
    }

    public void operateOneFieldByParamMap(Map queryParam, String updateField, Object updateValue, boolean upsert, boolean multi, String operateCmd) {
        Map<String, Object> operateMap = new HashMap();
        operateMap.put(updateField, updateValue);

        operateMulitFieldByParamMap(queryParam, operateMap, upsert, multi, operateCmd);
    }

    public void operateMulitFieldByParamMap(Map queryParam, Map operateMap, boolean upsert, boolean multi, String operateCmd) {
        DBCollection collection = getDAOCollection();

        DBObject query = new BasicDBObject();
        if (!EmptyUtil.isNullOrEmpty(queryParam)) {
            query.putAll(queryParam);
        }
        if (!EmptyUtil.isNullOrEmpty(operateMap)) {
            DBObject operateDoc = new BasicDBObject();
            operateDoc.putAll(operateMap);

            DBObject set = new BasicDBObject();
            set.put(operateCmd, operateDoc);

            collection.update(query, set, upsert, multi);
        }
    }

    public List<T> queryListByObjectId(ObjectId objectId, Class<? extends T> clazz, Object... sort) {
        return queryListByFieldValue(ConvertUtil._ID, objectId, clazz, sort);
    }

    public List<T> queryListByFieldValue(String queryField, Object queryValue, Class<? extends T> clazz, Object... sort) {
        DBObject query = null;
        if (!EmptyUtil.isNullOrEmpty(queryField)) {
            query = new BasicDBObject();
            query.put(queryField, queryValue);
        }
        return queryListByCursor(query, clazz, sort);
    }

    public List<T> queryListByParamMap(Map queryParam, Class<? extends T> clazz, Object... sort) {
        DBObject query = null;
        if (!EmptyUtil.isNullOrEmpty(queryParam)) {
            query = new BasicDBObject();
            query.putAll(queryParam);
        }
        return queryListByCursor(query, clazz, sort);
    }

    public List<T> queryListByObjectIdForPage(ObjectId objectId, Class<? extends T> clazz, int page, int pageSize, Object... sort) {
        return queryListByFieldValueForPage(ConvertUtil._ID, objectId, clazz, page, pageSize, new Object[0]);
    }

    public List<T> queryListByFieldValueForPage(String queryField, Object queryValue, Class<? extends T> clazz, int page, int pageSize, Object... sort) {
        DBObject query = null;
        if (!EmptyUtil.isNullOrEmpty(queryField)) {
            query = new BasicDBObject();
            query.put(queryField, queryValue);
        }
        return queryListByCursorForPage(query, clazz, page, pageSize, sort);
    }

    public List<T> queryListByParamMapForPage(Map queryParam, Class<? extends T> clazz, int page, int pageSize, Object... sort) {
        DBObject query = null;
        if (!EmptyUtil.isNullOrEmpty(queryParam)) {
            query = new BasicDBObject();
            query.putAll(queryParam);
        }
        return queryListByCursorForPage(query, clazz, page, pageSize, sort);
    }

    public int queryCountByObjectId(ObjectId objectId) {
        return queryCountByFieldValue(ConvertUtil._ID, objectId);
    }

    public int queryCountByFieldValue(String queryField, Object queryValue) {
        DBCollection collection = getDAOCollection();

        DBObject query = new BasicDBObject();
        query.put(queryField, queryValue);
        return (int) collection.count(query);
    }

    public int queryCountByParamMap(Map queryParam) {
        DBCollection collection = getDAOCollection();

        DBObject query = new BasicDBObject();
        if (!EmptyUtil.isNullOrEmpty(queryParam)) {
            query.putAll(queryParam);
        }
        return (int) collection.count(query);
    }

    public T queryOneByObjectId(ObjectId objectId, Class<? extends T> clazz) {
        return (T) queryOneByFieldValue(ConvertUtil._ID, objectId, clazz);
    }

    public T queryOneByFieldValue(String queryField, Object queryValue, Class<? extends T> clazz) {
        DBCollection collection = getDAOCollection();

        DBObject query = new BasicDBObject();
        query.put(queryField, queryValue);

        DBObject object = collection.findOne(query);
        return (T) ConvertUtil.dbObjectToBean(object, clazz);
    }

    public T queryOneByParamMap(Map queryParam, Class<? extends T> clazz) {
        DBCollection collection = getDAOCollection();

        DBObject query = new BasicDBObject();
        if (!EmptyUtil.isNullOrEmpty(queryParam)) {
            query.putAll(queryParam);
        }
        DBObject object = collection.findOne(query);
        return (T) ConvertUtil.dbObjectToBean(object, clazz);
    }

    public DBObject queryOneByObjectIdWithFilter(ObjectId objectId, List<String> requiredKeys) {
        return queryOneByFieldValueWithKeyFilter(ConvertUtil._ID, objectId, requiredKeys);
    }

    public DBObject queryOneByFieldValueWithKeyFilter(String queryField, Object queryValue, List<String> requiredKeys) {
        Map<String, Object> queryParam = new HashMap();
        queryParam.put(queryField, queryValue);

        return queryOneByParamMapWithKeyFilter(queryParam, requiredKeys);
    }

    public DBObject queryOneByParamMapWithKeyFilter(Map queryParam, List<String> requiredKeys) {
        DBCollection collection = getDAOCollection();

        DBObject query = new BasicDBObject();
        if (!EmptyUtil.isNullOrEmpty(queryParam)) {
            query.putAll(queryParam);
        }
        DBObject needDoc = new BasicDBObject();
        DBObject result;
        if (requiredKeys.size() > 0) {
            for (String key : requiredKeys) {
                needDoc.put(key, Integer.valueOf(1));
            }
            result = collection.findOne(query, needDoc);
        } else {
            result = collection.findOne(query);
        }
        return result;
    }

    public List<DBObject> queryListByObjectIdWithFilter(ObjectId objectId, List<String> requiredKeys) {
        return queryListByFieldValueWithKeyFilter(ConvertUtil._ID, objectId, requiredKeys);
    }

    public List<DBObject> queryListByFieldValueWithKeyFilter(String queryField, Object queryValue, List<String> requiredKeys) {
        Map<String, Object> queryParam = new HashMap();
        queryParam.put(queryField, queryValue);

        return queryListByParamMapWithKeyFilter(queryParam, requiredKeys);
    }

    public List<DBObject> queryListByParamMapWithKeyFilter(Map queryParam, List<String> requiredKeys) {
        DBCollection collection = getDAOCollection();

        DBObject query = null;
        if (!EmptyUtil.isNullOrEmpty(queryParam)) {
            query = new BasicDBObject();
            query.putAll(queryParam);
        }
        DBObject needDoc = new BasicDBObject();
        DBCursor cursor;
        if (requiredKeys.size() > 0) {
            for (String key : requiredKeys) {
                needDoc.put(key, Integer.valueOf(1));
            }
            cursor = collection.find(query, needDoc);
        } else {
            cursor = collection.find(query);
        }
        return cursor.toArray();
    }

    protected abstract DBCollection getDAOCollection();

    private List<T> queryListByCursorForPage(DBObject query, Class<? extends T> clazz, int page, int pageSize, Object... sort) {
        List<T> result = new ArrayList();
        DBCollection collection = getDAOCollection();
        DBCursor cursor;
        if (!EmptyUtil.isNullOrEmpty(sort)) {
            DBObject sortDoc = new BasicDBObject();
            for (int i = 0; i < sort.length; i += 2) {
                sortDoc.put(String.valueOf(sort[i]), Integer.valueOf(Integer.parseInt(String.valueOf(sort[(i + 1)]))));
            }
            cursor = collection.find(query).sort(sortDoc).skip((page - 1) * pageSize).limit(pageSize);
        } else {
            cursor = collection.find(query).skip((page - 1) * pageSize).limit(pageSize);
        }
        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            T instance = ConvertUtil.dbObjectToBean(object, clazz);
            result.add(instance);
        }
        return result;
    }

    private List<T> queryListByCursor(DBObject query, Class<? extends T> clazz, Object... sort) {
        List<T> result = new ArrayList();
        DBCollection collection = getDAOCollection();
        DBCursor cursor;
        if (!EmptyUtil.isNullOrEmpty(sort)) {
            DBObject sortDoc = new BasicDBObject();
            for (int i = 0; i < sort.length; i += 2) {
                sortDoc.put(String.valueOf(sort[i]), Integer.valueOf(Integer.parseInt(String.valueOf(sort[(i + 1)]))));
            }
            cursor = collection.find(query).sort(sortDoc);
        } else {
            cursor = collection.find(query);
        }
        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            T instance = ConvertUtil.dbObjectToBean(object, clazz);
            result.add(instance);
        }
        return result;
    }

    public List<DBObject> queryListByObject(DBObject query) {
        DBCollection collection = getDAOCollection();
        try {
            return collection.find(query).toArray();
        } catch (MongoException ex) {
            s_logger.error("queryListByObject_issue", ex);
            throw new MongoException("queryListByObject_issue", ex);
        }
    }

    public List<DBObject> queryListByObjectWithLimit(DBObject query, int limit) {
        return queryListByObjectWithLimitAndSort(query, limit, null);
    }

    public List<DBObject> queryListByObjectWithLimitAndSort(DBObject query, int limit, Object... sort) {
        DBCollection collection = getDAOCollection();
        if ((limit <= 0) || (limit >= 1000)) {
            limit = 1000;
        }
        try {
            if (!EmptyUtil.isNullOrEmpty(sort)) {
                DBObject sortDoc = new BasicDBObject();
                for (int i = 0; i < sort.length; i += 2) {
                    sortDoc.put(String.valueOf(sort[i]), Integer.valueOf(Integer.parseInt(String.valueOf(sort[(i + 1)]))));
                }
                return collection.find(query).limit(limit).sort(sortDoc).toArray();
            }
            return collection.find(query).limit(limit).toArray();
        } catch (MongoException ex) {
            s_logger.error("queryListByObjectWithLimitAndSort_issue", ex);
            throw new MongoException("queryListByObjectWithLimitAndSort_issue", ex);
        }
    }

    public WriteResult updateOnce(DBObject q, DBObject o, boolean upsert, boolean multi, WriteConcern concern) {
        DBCollection collection = getDAOCollection();
        return collection.update(q, o, upsert, multi, concern);
    }

    public int updateMajority(DBObject q, DBObject o, boolean upsert) {
        return update(q, o, upsert, false, WriteConcern.SAFE);
    }

    public int update(DBObject q, DBObject o, boolean upsert, boolean muilt, WriteConcern concern) {
        WriteResult res = null;
        try {
            res = updateOnce(q, o, upsert, muilt, concern);
        } catch (MongoException ex) {
            s_logger.error("update_issue", ex);
        }
        return res.getN();
    }

    public WriteResult remove(DBObject obj) {
        try {
            DBCollection collection = getDAOCollection();
            return collection.remove(obj);
        } catch (MongoException e) {
            s_logger.error("remove_issue", e);
            throw new MongoException("remove_issue", e);
        }
    }

    public List<DBObject> mapReduce(DBObject matchWrap, String map, String reduce) {
        List<DBObject> resultList;
        try {
            DBCollection collection = getDAOCollection();
            MapReduceCommand cmd = new MapReduceCommand(collection, map, reduce, null, MapReduceCommand.OutputType.INLINE, matchWrap);
            MapReduceOutput fileAudits = collection.mapReduce(cmd);
            Iterator<DBObject> iterator = fileAudits.results().iterator();
            resultList = new ArrayList();
            while (iterator.hasNext()) {
                resultList.add(iterator.next());
            }
        } catch (MongoException e) {
            s_logger.error("mapReduce_issue", e);
            throw new MongoException("mapReduce_issue", e);
        }
        return resultList;
    }

    public List<DBObject> aggregateList(DBObject matchWrap, DBObject projectWrap, DBObject groupWrap) {
        List<DBObject> resultList;
        try {
            DBCollection collection = getDAOCollection();
            AggregationOutput output = collection.aggregate(Arrays.asList(new DBObject[]{matchWrap, projectWrap, groupWrap}));
            Iterator<DBObject> iterator = output.results().iterator();
            resultList = new ArrayList();
            while (iterator.hasNext()) {
                resultList.add(iterator.next());
            }
        } catch (MongoException e) {
            s_logger.error("aggregateList_issue", e);
            throw new MongoException("aggregateList_issue", e);
        }
        return resultList;
    }
}
