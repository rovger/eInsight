package com.eInsight.common.dao;

import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

interface MongoDAO<T> {
    void save(T paramT);

    ObjectId save(T paramT, boolean paramBoolean);

    void insert(T paramT);

    ObjectId insert(T paramT, boolean paramBoolean);

    void updateBeanByObjectId(ObjectId paramObjectId, T paramT);

    void updateBeanByFieldValue(String paramString, Object paramObject, T paramT);

    void updateBeanByParamMap(Map paramMap, T paramT);

    void updateFieldsByObjectId(ObjectId paramObjectId, Map paramMap);

    void updateFieldsByFieldValue(String paramString, Object paramObject, Map paramMap);

    void updateFieldsByParamMap(Map paramMap1, Map paramMap2);

    void removeByObjectId(ObjectId paramObjectId);

    void removeByFieldValue(String paramString, Object paramObject);

    void removeByParamMap(Map paramMap);

    void setOneFieldByObjectId(ObjectId paramObjectId, String paramString, Object paramObject);

    void setOneFieldByFieldValue(String paramString1, Object paramObject1, String paramString2, Object paramObject2);

    void setOneFieldByParamMap(Map paramMap, String paramString, Object paramObject);

    void setOneFieldByFieldValue(String paramString1, Object paramObject1, String paramString2, Object paramObject2, boolean paramBoolean1, boolean paramBoolean2);

    void setOneFieldByParamMap(Map paramMap, String paramString, Object paramObject, boolean paramBoolean1, boolean paramBoolean2);

    void setMultiFieldByObjectId(ObjectId paramObjectId, Map paramMap);

    void setMultiFieldByFieldValue(String paramString, Object paramObject, Map paramMap);

    void setMultiFieldByParamMap(Map paramMap1, Map paramMap2);

    void setMultiFieldByFieldValue(String paramString, Object paramObject, Map paramMap, boolean paramBoolean1, boolean paramBoolean2);

    void setMultiFieldByParamMap(Map paramMap1, Map paramMap2, boolean paramBoolean1, boolean paramBoolean2);

    void increaseOneFieldNumberByObjectId(ObjectId paramObjectId, String paramString, Number paramNumber);

    void increaseMultiFieldNumberByObjectId(ObjectId paramObjectId, Map paramMap);

    void increaseOneFieldNumberByFieldValue(String paramString1, Object paramObject, String paramString2, Number paramNumber);

    void increaseMultiFieldNumberByFieldValue(String paramString, Object paramObject, Map paramMap);

    void increaseOneFieldNumberByParamMap(Map paramMap, String paramString, Number paramNumber);

    void increaseMultiFieldNumberByParamMap(Map paramMap1, Map paramMap2);

    void increaseOneFieldNumberByFieldValue(String paramString1, Object paramObject, String paramString2, Number paramNumber, boolean paramBoolean1, boolean paramBoolean2);

    void increaseMultiFieldNumberByFieldValue(String paramString, Object paramObject, Map paramMap, boolean paramBoolean1, boolean paramBoolean2);

    void increaseOneFieldNumberByParamMap(Map paramMap, String paramString, Number paramNumber, boolean paramBoolean1, boolean paramBoolean2);

    void increaseMultiFieldNumberByParamMap(Map paramMap1, Map paramMap2, boolean paramBoolean1, boolean paramBoolean2);

    void operateOneFieldByObjectId(ObjectId paramObjectId, String paramString1, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, String paramString2);

    void operateMultiFieldByObjectId(ObjectId paramObjectId, Map paramMap, boolean paramBoolean1, boolean paramBoolean2, String paramString);

    void operateOneFieldByFieldValue(String paramString1, Object paramObject1, String paramString2, Object paramObject2, boolean paramBoolean1, boolean paramBoolean2, String paramString3);

    void operateMultiFieldByFieldValue(String paramString1, Object paramObject, Map paramMap, boolean paramBoolean1, boolean paramBoolean2, String paramString2);

    void operateOneFieldByParamMap(Map paramMap, String paramString1, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, String paramString2);

    void operateMulitFieldByParamMap(Map paramMap1, Map paramMap2, boolean paramBoolean1, boolean paramBoolean2, String paramString);

    List<T> queryListByObjectId(ObjectId paramObjectId, Class<? extends T> paramClass, Object... paramVarArgs);

    List<T> queryListByFieldValue(String paramString, Object paramObject, Class<? extends T> paramClass, Object... paramVarArgs);

    List<T> queryListByParamMap(Map paramMap, Class<? extends T> paramClass, Object... paramVarArgs);

    List<T> queryListByObjectIdForPage(ObjectId paramObjectId, Class<? extends T> paramClass, int paramInt1, int paramInt2, Object... paramVarArgs);

    List<T> queryListByFieldValueForPage(String paramString, Object paramObject, Class<? extends T> paramClass, int paramInt1, int paramInt2, Object... paramVarArgs);

    List<T> queryListByParamMapForPage(Map paramMap, Class<? extends T> paramClass, int paramInt1, int paramInt2, Object... paramVarArgs);

    int queryCountByObjectId(ObjectId paramObjectId);

    int queryCountByFieldValue(String paramString, Object paramObject);

    int queryCountByParamMap(Map paramMap);

    T queryOneByObjectId(ObjectId paramObjectId, Class<? extends T> paramClass);

    T queryOneByFieldValue(String paramString, Object paramObject, Class<? extends T> paramClass);

    T queryOneByParamMap(Map paramMap, Class<? extends T> paramClass);

    DBObject queryOneByObjectIdWithFilter(ObjectId paramObjectId, List<String> paramList);

    DBObject queryOneByFieldValueWithKeyFilter(String paramString, Object paramObject, List<String> paramList);

    DBObject queryOneByParamMapWithKeyFilter(Map paramMap, List<String> paramList);

    List<DBObject> queryListByObjectIdWithFilter(ObjectId paramObjectId, List<String> paramList);

    List<DBObject> queryListByFieldValueWithKeyFilter(String paramString, Object paramObject, List<String> paramList);

    List<DBObject> queryListByParamMapWithKeyFilter(Map paramMap, List<String> paramList);

    List<DBObject> queryListByObject(DBObject paramDBObject);

    List<DBObject> queryListByObjectWithLimit(DBObject paramDBObject, int paramInt);

    List<DBObject> queryListByObjectWithLimitAndSort(DBObject paramDBObject, int paramInt, Object... paramVarArgs);

    WriteResult updateOnce(DBObject paramDBObject1, DBObject paramDBObject2, boolean paramBoolean1, boolean paramBoolean2, WriteConcern paramWriteConcern);

    int update(DBObject paramDBObject1, DBObject paramDBObject2, boolean paramBoolean1, boolean paramBoolean2, WriteConcern paramWriteConcern);

    WriteResult remove(DBObject paramDBObject);

    List<DBObject> mapReduce(DBObject paramDBObject, String paramString1, String paramString2);

    List<DBObject> aggregateList(DBObject paramDBObject1, DBObject paramDBObject2, DBObject paramDBObject3);
}


/* Location:              C:\Users\weijlu\Desktop\eInsight.war!\WEB-INF\classes\com\eInsight\common\dao\MongoDAO.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */