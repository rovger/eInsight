package com.eInsight.task.pullTask;

import com.eInsight.common.dao.impl.RawDataDAOImpl;
import com.eInsight.common.utils.EmptyUtil;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PullDataCallBack {
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd.HH-mm-ss");

    public String buildURL(String url, Date startDate, Date endDate, String... params) {
        return MessageFormat.format(url, new Object[]{this.format.format(startDate), this.format.format(endDate)});
    }

    public List<Object> buildMap(String json) {
        if ((EmptyUtil.isNullOrEmpty(json)) || ("[]".equals(json))) {
            return new ArrayList();
        }
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        List<Object> list = JsonPath.read(document, "$.[*]", new Predicate[0]);
        return list;
    }

    public void saveDate(Date startDate, String url, Object objMap) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("content", objMap);
        dbObject.put("CreationDate", startDate);
        dbObject.put("url", url);
        RawDataDAOImpl.getRawDataDAOInstance().save(dbObject);
    }
}
