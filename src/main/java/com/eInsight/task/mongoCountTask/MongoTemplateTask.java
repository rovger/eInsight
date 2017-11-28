package com.eInsight.task.mongoCountTask;

import com.eInsight.common.dao.ConsoleDAOImpl;
import com.eInsight.common.dao.impl.AdvancedReportDAOImpl;
import com.eInsight.common.dao.impl.RawDataDAOImpl;
import com.eInsight.common.dao.impl.ReportDAOImpl;
import com.eInsight.common.utils.EmptyUtil;
import com.eInsight.task.common.QueryConditionResultObject;
import com.eInsight.task.common.TaskTemplate;
import com.eInsight.task.common.TimeUnit;
import com.eInsight.task.common.ValueMappingCallBack;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

public class MongoTemplateTask extends TaskTemplate {
    private static Logger s_logger = Logger.getLogger(MongoTemplateTask.class.getSimpleName());
    private String mongoTemplateName;
    private ConsoleDAOImpl fromDAO;
    private ConsoleDAOImpl toDAO;
    private Map<String, Object> matchMap;

    public MongoTemplateTask(String taskName, TimeUnit taskTimeType, String mongoTemplateName) {
        super(taskName, taskTimeType);
        this.mongoTemplateName = mongoTemplateName;
    }

    public MongoTemplateTask(String taskName, TimeUnit taskTimeType, String mongoTemplateName, long taskoffset) {
        super(taskName, taskTimeType, taskoffset);
        this.mongoTemplateName = mongoTemplateName;
    }

    public MongoTemplateTask(String taskName, TimeUnit taskTimeType, String mongoTemplateName, ValueMappingCallBack valueCallBack, Map<String, Object> matchMap, long taskoffset) {
        super(taskName, taskTimeType, valueCallBack, taskoffset);
        this.mongoTemplateName = mongoTemplateName;
        this.matchMap = matchMap;
    }

    protected void doCountTask(Date startDate, Date endDate) throws Exception {
        String taskName = getTaskName();
        TimeUnit taskTimeType = getTaskTimeType();
        if (taskTimeType == TimeUnit.MINUTELY) {
            this.fromDAO = RawDataDAOImpl.getRawDataDAOInstance();
            this.toDAO = ReportDAOImpl.getReportDAOInstace();
        } else if (taskTimeType == TimeUnit.HOURLY) {
            this.mongoTemplateName = "sysTaskTemplate/HourlyCount";
            this.fromDAO = ReportDAOImpl.getReportDAOInstace();
            this.toDAO = AdvancedReportDAOImpl.getAdvancedReportDAOInstace();
        } else {
            this.mongoTemplateName = "sysTaskTemplate/DailyCount";
            this.fromDAO = AdvancedReportDAOImpl.getAdvancedReportDAOInstace();
            this.toDAO = AdvancedReportDAOImpl.getAdvancedReportDAOInstace();
        }
        MongoTemplateBuilder mongoBuilder = new MongoTemplateBuilder(this.mongoTemplateName);
        Map<String, Object> dataMap = mongoBuilder.getDataMap();
        Date startTime = new Date(startDate.getTime() - super.getTaskoffset());
        Date endTime = new Date(endDate.getTime() - super.getTaskoffset());
        dataMap.put("startTime", DateUtilLong2String(startTime.getTime()));
        dataMap.put("endTime", DateUtilLong2String(endTime.getTime()));
        dataMap.put("taskName", taskName);
        dataMap.put("timeMatrix", taskTimeType.getName());
        customizeDataMap(dataMap);
        QueryConditionResultObject mongoQuery = mongoBuilder.getQueryConditionObjects();
        DBObject matchCondition = mongoQuery.getMatchObject();
        if ((this.matchMap != null) && (this.matchMap.size() > 0)) {
            DBObject matchObj = (DBObject) matchCondition.get("$match");
            matchObj.putAll(this.matchMap);
            matchCondition.put("$match", matchObj);
            mongoQuery.setMatchObject(matchCondition);
        }
        List<DBObject> resultlist = null;
        if (this.mongoTemplateName.equals("MapReduce")) {
            resultlist = this.fromDAO.mapReduce(
                    (DBObject) mongoQuery.getMatchObject().get("$match"),
                    (String) mongoQuery.getProjectObject().get("mapScript"),
                    (String) mongoQuery.getGroupObject().get("reduceScript"));
        } else {
            resultlist = this.fromDAO.aggregateList(mongoQuery.getMatchObject(), mongoQuery.getProjectObject(), mongoQuery.getGroupObject());
        }
        if ((resultlist != null) && (resultlist.size() > 0)) {
            List<DBObject> mergedResultList = new ArrayList();
            for (DBObject countItem : resultlist) {
                DBObject document = new BasicDBObject();
                document.put("ColumnName", taskName);
                DBObject colomnvalue;
                if (this.mongoTemplateName.equals("MapReduce")) {
                    List<String> countbylist = new ArrayList();
                    countbylist.add((String) countItem.get("_id"));
                    String columnValue = super.getValueCallBack().callback(countbylist);
                    if (EmptyUtil.isNullOrEmpty(columnValue)) {
                        continue;
                    }
                    document.put("ColumnValue", columnValue);
                    colomnvalue = (DBObject) countItem.get("value");
                    document.put("Count", Long.valueOf(Math.round(Double.parseDouble(colomnvalue.get("count").toString()))));
                } else {
                    DBObject idJson = (DBObject) countItem.get("_id");
                    List<String> countbylist = new ArrayList();
                    for (String countbykey : idJson.keySet()) {
                        String countby = String.valueOf(idJson.get(countbykey));
                        if (!countby.equals("null")) {
                            countbylist.add(countby);
                        }
                    }
                    String columnValue = super.getValueCallBack().callback(countbylist);
                    if (EmptyUtil.isNullOrEmpty(columnValue)) {
                        continue;
                    }
                    document.put("ColumnValue", columnValue);
                    document.put("Count", Long.valueOf(Long.parseLong(countItem.get("count").toString())));
                }
                document.put("StartDate", new Date(startDate.getTime() - super.getTaskoffset()));
                document.put("EndDate", new Date(endDate.getTime() - super.getTaskoffset()));
                document.put("CreationDate", new Date());
                document.put("TimeMatrix", taskTimeType.getName());

                DBObject existeddocument = match(mergedResultList, document);
                if (existeddocument == null) {
                    mergedResultList.add(document);
                } else {
                    long exitedCount = Long.parseLong(existeddocument.get("Count").toString());
                    existeddocument.put("Count", Long.valueOf(exitedCount + Long.parseLong(countItem.get("count").toString())));
                }
            }
            List<DBObject> dbItemList = findByColumnName(this.toDAO, taskName, taskTimeType, startDate, endDate);
            if ((dbItemList != null) && (dbItemList.size() > 0)) {
                DBObject query;
                for (DBObject dbItem : dbItemList) {
                    query = new BasicDBObject("_id", dbItem.get("_id"));
                    WriteResult writeResult = this.toDAO.remove(query);
                    s_logger.info(writeResult);
                }
            }
            for (DBObject countItem : mergedResultList) {
                ObjectId objectId = this.toDAO.save(countItem, true);
                s_logger.info("ObjectId: " + objectId + " has been saved!");
            }
        }
    }

    private DBObject match(List<DBObject> mergedList, DBObject document) {
        if ((mergedList == null) || (mergedList.size() == 0)) {
            return null;
        }
        String columnName = document.get("ColumnName").toString();
        String columnValue = document.get("ColumnValue").toString();
        if ((EmptyUtil.isNullOrEmpty(columnName)) || (EmptyUtil.isNullOrEmpty(columnValue))) {
            return null;
        }
        long startTime = ((Date) document.get("StartDate")).getTime();
        long endTime = ((Date) document.get("EndDate")).getTime();
        for (DBObject item : mergedList) {
            String columnName0 = item.get("ColumnName").toString();
            String columnValue0 = item.get("ColumnValue").toString();
            long startTime0 = ((Date) item.get("StartDate")).getTime();
            long endTime0 = ((Date) item.get("EndDate")).getTime();
            if ((columnName.equals(columnName0)) &&
                    (columnValue.equals(columnValue0)) && (startTime - startTime0 == 0L) && (endTime - endTime0 == 0L)) {
                return item;
            }
        }
        return null;
    }

    private List<DBObject> findByColumnName(ConsoleDAOImpl toDAO, String columnName, TimeUnit taskTimeType, Date startDate, Date endDate) {
        DBObject query = new BasicDBObject();
        query.put("ColumnName", columnName);
        query.put("TimeMatrix", taskTimeType.getName());
        DBObject timeGap = new BasicDBObject();
        timeGap.put("$gte", startDate);
        timeGap.put("$lt", endDate);
        query.put("StartDate", timeGap);
        return toDAO.queryListByObject(query);
    }

    protected void customizeDataMap(Map<String, Object> dataMap) {
    }

    private String DateUtilLong2String(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(new Date(time));
    }

    public Map<String, Object> getMatchMap() {
        return this.matchMap;
    }

    public String toString() {
        return "MongoTemplateTask [mongoTemplateName=" + this.mongoTemplateName + ", toString()=" + super.toString() + "]";
    }
}
