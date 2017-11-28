package com.eInsight.resources;

import com.eInsight.common.Initializer;
import com.eInsight.common.dao.ConsoleDAOImpl;
import com.eInsight.common.dao.impl.AdvancedReportDAOImpl;
import com.eInsight.common.dao.impl.ReportDAOImpl;
import com.eInsight.common.utils.EmptyUtil;
import com.eInsight.common.utils.JSONObjectIdAdapter;
import com.eInsight.task.common.TaskTemplate;
import com.eInsight.task.common.TimeUnit;
import com.eInsight.task.mongoCountTask.MongoMapReduceTask;
import com.eInsight.task.mongoCountTask.MongoTemplateTask;
import com.eInsight.task.mongoCountTask.MongoTemplateTaskMapping;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
@Path("/report")
public class ReportService {
    private static Logger s_logger = Logger.getLogger(ReportService.class);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    @Context
    HttpServletRequest servletRequest;

    @GET
    @Path("rawdatas/{columnname}/{columnvalue}/{startdate}/{enddate}/{limit}")
    @Produces({"application/json"})
    public String getRawdatasByTimeGap(
            @PathParam("columnname") String columnname,
            @PathParam("columnvalue") String columnvalue,
            @PathParam("limit") int limit,
            @PathParam("startdate") String startdateStr,
            @PathParam("enddate") String enddateStr) {
        BasicDBObject rawquerycondition = new BasicDBObject();
        BasicDBObject querycondition = new BasicDBObject();
        querycondition.put("ColumnName", columnname);
        querycondition.put("ColumnValue", columnvalue);

        BasicDBObject offset = new BasicDBObject();
        s_logger.info("getRawsByTimeGap::startdateStr" + startdateStr);
        s_logger.info("getRawsByTimeGap::enddateStr" + enddateStr);
        try {
            try {
                long reportOffset = 0L;
                if ((startdateStr.length() > 0) && (enddateStr.length() > 0)) {
                    Long startdate = Long.valueOf(this.sdf.parse(startdateStr).getTime() - reportOffset);
                    Long enddate = Long.valueOf(this.sdf.parse(enddateStr).getTime() - reportOffset);
                    offset.put("$gte", new Date(startdate.longValue()));
                    offset.put("$lt", new Date(enddate.longValue()));
                } else if ((startdateStr.length() > 0) && (enddateStr.isEmpty())) {
                    Long startdate = Long.valueOf(this.sdf.parse(startdateStr).getTime() - reportOffset);
                    offset.put("$gte", new Date(startdate.longValue()));
                } else if ((startdateStr.isEmpty()) && (enddateStr.length() > 0)) {
                    Long enddate = Long.valueOf(this.sdf.parse(enddateStr).getTime() - reportOffset);
                    offset.put("$lt", new Date(enddate.longValue()));
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            if (offset.size() > 0) {
                querycondition.put("StartDate", offset);
            }
            s_logger.info("getRawsByTimeGap::query report condition" + new Gson().toJson(querycondition));
            List<DBObject> resultlist = ReportDAOImpl.getReportDAOInstace().queryListByObjectWithLimit(querycondition, limit);
            s_logger.info("getRawsByTimeGap::query report result" + new Gson().toJson(resultlist));
            List currTimegapOrList = new ArrayList();
            long totalcount = 0L;
            for (DBObject report : resultlist) {
                Date startDate = (Date) report.get("StartDate");
                Date endDate = (Date) report.get("EndDate");
                long count = ((Long) report.get("Count")).longValue();
                totalcount += count;
                BasicDBObject createdatetimegap = new BasicDBObject();
                BasicDBObject timegap = new BasicDBObject();
                timegap.put("$gte", startDate);
                timegap.put("$lt", endDate);
                createdatetimegap.put("CreationDate", timegap);
                currTimegapOrList.add(createdatetimegap);
                if ((limit > 0) && (totalcount > limit)) {
                    break;
                }
            }
            if (currTimegapOrList.size() > 0) {
                rawquerycondition.put("$or", currTimegapOrList);
                s_logger.info("getRawsByTimeGap::query raw time condition" + new Gson().toJson(currTimegapOrList));
                List<String> columnvalueList = null;
                Map matchcondition = new HashMap();
                boolean isMapReduceTask = false;
                for (TaskTemplate task : Initializer.getTasks()) {
                    if ((task.getTaskTimeType() == TimeUnit.MINUTELY) && (columnname.equals(task.getTaskName()))) {
                        columnvalueList = task.getValueCallBack().reverseCallback(columnvalue);
                        if ((task instanceof MongoMapReduceTask)) {
                            isMapReduceTask = true;
                        }
                        if (!(task instanceof MongoTemplateTask)) {
                            break;
                        }
                        MongoTemplateTask countbypathtask = (MongoTemplateTask) task;
                        matchcondition = countbypathtask.getMatchMap();
                        break;
                    }
                }
                if ((matchcondition != null) && (!matchcondition.isEmpty())) {
                    rawquerycondition.putAll(matchcondition);
                    s_logger.info("getRawsByTimeGap::query raw task match condition" + new Gson().toJson(matchcondition));
                }
                Map<String, Object> mapreducecondition = new HashMap();
                if (isMapReduceTask) {
                    mapreducecondition = new Gson().fromJson(columnvalueList.get(0), Map.class);
                    if ((mapreducecondition != null) && (!mapreducecondition.isEmpty())) {
                        rawquerycondition.putAll(mapreducecondition);
                        s_logger.info("getRawsByTimeGap::query raw mapreuduce match condition" + new Gson().toJson(mapreducecondition));
                    }
                }
                Map<String, Object> conunByPathcondition = new HashMap();
                if ((MongoTemplateTaskMapping.getCountByPath(columnname) != null) && (MongoTemplateTaskMapping.getCountByPath(columnname).length > 0)) {
                    for (int i = 0; i < columnvalueList.size(); i++) {
                        try {
                            conunByPathcondition.put(MongoTemplateTaskMapping.getCountByPath(columnname)[i], new Gson().fromJson(columnvalueList.get(i), BasicDBObject.class));
                        } catch (Exception e) {
                            conunByPathcondition.put(MongoTemplateTaskMapping.getCountByPath(columnname)[i], (columnvalueList).get(i));
                        }
                    }
                    if ((conunByPathcondition != null) && (!conunByPathcondition.isEmpty())) {
                        rawquerycondition.putAll(conunByPathcondition);
                        s_logger.info("getRawsByTimeGap::query raw reverse callback condition" + new Gson().toJson(conunByPathcondition));
                    }
                }
                if (((matchcondition == null) || (matchcondition.isEmpty())) && ((mapreducecondition == null)
                        || (mapreducecondition.isEmpty())) && ((conunByPathcondition == null) || (conunByPathcondition.isEmpty()))) {
                    throw new RuntimeException("No match condition!");
                }
                RawDataService rawService = new RawDataService();
                s_logger.info("getRawsByTimeGap::query raw all condition" + new Gson().toJson(rawquerycondition));
                return rawService.findByConditionLimt(limit, rawquerycondition);
            }
            return buildResponse(rawquerycondition, new ArrayList());
        } catch (Exception e) {
            s_logger.error("getRawdatasByTimeGap_issue", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @GET
    @Path("/findbytimerange/{reportpath}/{starttime}/{endtime}/{daysoffset}/{limit}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public String findByTimeRange(
            @PathParam("reportpath") String reportpath,
            @PathParam("starttime") String starttime,
            @PathParam("endtime") String endtime,
            @PathParam("daysoffset") int daysoffset,
            @PathParam("limit") int limit) {
        return findTimeRangeHistory(reportpath, starttime, endtime, daysoffset, limit, "", 0);
    }

    @GET
    @Path("/findbytimerange/{reportpath}/{starttime}/{endtime}/{daysoffset}/{limit}/{historydatematrix}/{historydateoffset}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public String findTimeRangeHistory(
            @PathParam("reportpath") String reportpath,
            @PathParam("starttime") String starttime,
            @PathParam("endtime") String endtime,
            @PathParam("daysoffset") int daysoffset,
            @PathParam("limit") int limit,
            @PathParam("historydatematrix") String historydatematrix,
            @PathParam("historydateoffset") int historydateoffset) {
        ConsoleDAOImpl dao = AdvancedReportDAOImpl.getAdvancedReportDAOInstace();
        BasicDBObject querycondition = new BasicDBObject();
        querycondition.put("ColumnName", reportpath);
        BasicDBObject timegap = new BasicDBObject();
        Date start;
        Date end;
        if (!"".equals(historydatematrix)) {
            start = getTruncateTimeGap(starttime, historydatematrix, historydateoffset);
            end = getTruncateTimeGap(endtime, historydatematrix, historydateoffset);
        } else {
            try {
                start = this.sdf.parse(starttime);
                end = this.sdf.parse(endtime);
            } catch (Exception e) {
                s_logger.error("findTimeRangeHistory", e);
                throw new RuntimeException(e.getMessage());
            }
        }
        start = DateUtils.addDays(start, daysoffset);
        end = DateUtils.addDays(end, daysoffset);
        if (end.getTime() - start.getTime() <= 2 * 60 * 60 * 1000L) {
            dao = ReportDAOImpl.getReportDAOInstace();
            querycondition.put("TimeMatrix", TimeUnit.MINUTELY.getName());
        } else if (end.getTime() - start.getTime() <= 7 * 24 * 60 * 60 * 1000L) {
            querycondition.put("TimeMatrix", TimeUnit.HOURLY.getName());
        } else {
            querycondition.put("TimeMatrix", TimeUnit.DAILY.getName());
        }
        timegap.put("$gte", start);
        timegap.put("$lt", end);
        querycondition.put("StartDate", timegap);
        return findByConditionLimt(limit, querycondition, dao);
    }

    @GET
    @Path("/findbytimedetail/{reportpath}/{datematrix}/{startdateoffset}/{enddateoffset}/{limit}/{reportdatatimematrix}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public String findByReportTimeMatrix(
            @PathParam("reportpath") String reportpath,
            @PathParam("datematrix") String datematrix,
            @PathParam("startdateoffset") int startdateoffset,
            @PathParam("enddateoffset") int enddateoffset,
            @PathParam("limit") int limit,
            @PathParam("reportdatatimematrix") String reportdatatimematrix) {
        return findByReportTimeMatrixAndHistoryDateOffset(reportpath, datematrix, startdateoffset, enddateoffset, limit, reportdatatimematrix, "", 0);
    }

    @GET
    @Path("/findbytimedetail/{reportpath}/{datematrix}/{startdateoffset}/{enddateoffset}/{limit}/{reportdatatimematrix}/{historydatematrix}/{historydateoffset}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public String findByReportTimeMatrixAndHistoryDateOffset(
            @PathParam("reportpath") String reportpath,
            @PathParam("datematrix") String datematrix,
            @PathParam("startdateoffset") int startdateoffset,
            @PathParam("enddateoffset") int enddateoffset,
            @PathParam("limit") int limit,
            @PathParam("reportdatatimematrix") String reportdatatimematrix,
            @PathParam("historydatematrix") String historydatematrix,
            @PathParam("historydateoffset") int historydateoffset) {
        ConsoleDAOImpl dao = ReportDAOImpl.getReportDAOInstace();
        BasicDBObject querycondition = new BasicDBObject();
        querycondition.put("ColumnName", reportpath);
        BasicDBObject timegap = null;
        long reportOffset = 0L;
        for (TaskTemplate task : Initializer.getTasks()) {
            if (task.getTaskName().equals(reportpath)) {
                reportOffset = task.getTaskoffset();
                break;
            }
        }
        Date baseDate = getTruncateBaseDate(historydatematrix, historydateoffset);
        baseDate = new Date(baseDate.getTime() - reportOffset);
        datematrix = datematrix.toUpperCase();
        if (datematrix.equals(TimeUnit.MINUTELY.getName())) {
            timegap = getTimegap(baseDate, datematrix, startdateoffset, enddateoffset);
        } else if (datematrix.equals(TimeUnit.HOURLY.getName())) {
            timegap = getTimegap(baseDate, datematrix, startdateoffset, enddateoffset);
        } else if (datematrix.equals(TimeUnit.DAILY.getName())) {
            timegap = getTimegap(baseDate, datematrix, startdateoffset, enddateoffset);
            querycondition.put("TimeMatrix", TimeUnit.HOURLY.getName());
        } else if (datematrix.equals(TimeUnit.WEEKLY.getName())) {
            timegap = getTimegap(baseDate, datematrix, startdateoffset, enddateoffset);
            querycondition.put("TimeMatrix", TimeUnit.DAILY.getName());
        } else if (datematrix.equals(TimeUnit.MONTHLY.getName())) {
            timegap = getTimegap(baseDate, datematrix, startdateoffset, enddateoffset);
            querycondition.put("TimeMatrix", TimeUnit.DAILY.getName());
        }
        if (!EmptyUtil.isNullOrEmpty(reportdatatimematrix)) {
            reportdatatimematrix = reportdatatimematrix.toUpperCase();
            if ((reportdatatimematrix.equals(TimeUnit.HOURLY.getName()))
                    || (reportdatatimematrix.equals(TimeUnit.DAILY.getName()))
                    || (reportdatatimematrix.equals(TimeUnit.WEEKLY.getName()))) {
                querycondition.put("TimeMatrix", reportdatatimematrix.toUpperCase());
                dao = AdvancedReportDAOImpl.getAdvancedReportDAOInstace();
            }
            if (reportdatatimematrix.equals(TimeUnit.HOURLY.getName())) {
                timegap.put("$gte", DateUtils.setMinutes((Date) timegap.get("$gte"), 0));
                timegap.put("$lt", DateUtils.setMinutes((Date) timegap.get("$lt"), 0));
            }
            if (reportdatatimematrix.equals(TimeUnit.DAILY.getName())) {
                timegap.put("$gte", DateUtils.setHours((Date) timegap.get("$gte"), 0));
                timegap.put("$lt", DateUtils.setHours((Date) timegap.get("$lt"), 0));
            }
        }
        querycondition.put("StartDate", timegap);
        return findByConditionLimt(limit, querycondition, dao);
    }

    private String findByConditionLimt(int limit, BasicDBObject querycondition, ConsoleDAOImpl dao) {
        String response = null;
        try {
            List<DBObject> resultlist = dao.queryListByObjectWithLimit(querycondition, limit);
            response = buildResponse(querycondition, resultlist);
        } catch (Exception e) {
            s_logger.error("findByConditionLimt", e);
        }
        if (response == null) {
            response = "{'error':'mongo path may be wrong or not existed " + new Gson().toJson(querycondition) + "'";
        }
        return response;
    }

    private String buildResponse(DBObject object, List<DBObject> dbobjectlist) {
        LinkedHashMap<String, Object> result = new LinkedHashMap();
        LinkedHashMap<String, Object> summary = new LinkedHashMap();
        summary.put("queryCondition", object);
        DBObject startDate = (DBObject) object.get("StartDate");
        if (startDate != null) {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
            result.put("start_time", format.format(startDate.get("$gte")));
            result.put("end_time", format.format(startDate.get("$lt")));
        }
        summary.put("count", Integer.valueOf(dbobjectlist.size()));
        result.put("summary", summary);
        result.put("detail", dbobjectlist);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        result.put("start", sdf.format(startDate.get("$gte")));
        result.put("end", sdf.format(startDate.get("$lt")));

        long rangeOffset = 0L;
        Date startOffset;
        Date endOffset;
        try {
            startOffset = sdf.parse((String) result.get("start"));
            endOffset = sdf.parse((String) result.get("end"));
        } catch (Exception e) {
            s_logger.error("buildResponse", e);
            throw new RuntimeException(e.getMessage());
        }
        String timeMatrix = (String) object.get("TimeMatrix");
        if (TimeUnit.MINUTELY.getName().equals(timeMatrix)) {
            rangeOffset = (startOffset.getTime() - endOffset.getTime()) / (60 * 1000L);
        } else if (TimeUnit.HOURLY.getName().equals(timeMatrix)) {
            rangeOffset = (startOffset.getTime() - endOffset.getTime()) / (60 * 60 * 1000L);
        } else if (TimeUnit.DAILY.getName().equals(timeMatrix)) {
            rangeOffset = (startOffset.getTime() - endOffset.getTime()) / (24 * 60 * 60 * 1000L);
        }
        result.put("rangeOffset", Long.valueOf(rangeOffset));

        String response = new GsonBuilder().serializeNulls().registerTypeAdapter(ObjectId.class, new JSONObjectIdAdapter()).create().toJson(result);
        return response;
    }

    private BasicDBObject getTimegap(Date date, String datematrix, int startdateoffset, int enddateoffset) {
        Date startDate = null;
        Date endDate = null;
        BasicDBObject timegap = new BasicDBObject();
        Calendar c = Calendar.getInstance();
        if (datematrix.equals(TimeUnit.DAILY.getName())) {
            date = DateUtils.setMilliseconds(date, 0);
            date = DateUtils.setSeconds(date, 0);
            date = DateUtils.setMinutes(date, 0);
            date = DateUtils.setHours(date, 0);
            startDate = DateUtils.addDays(date, startdateoffset);
            endDate = DateUtils.addDays(date, enddateoffset);
        } else if (datematrix.equals(TimeUnit.HOURLY.getName())) {
            date = DateUtils.setMilliseconds(date, 0);
            date = DateUtils.setSeconds(date, 0);
            startDate = DateUtils.addHours(date, startdateoffset);
            endDate = DateUtils.addHours(date, enddateoffset);
        } else if (datematrix.equals(TimeUnit.MINUTELY.getName())) {
            date = DateUtils.setMilliseconds(date, 0);
            date = DateUtils.setSeconds(date, 0);
            startDate = DateUtils.addMinutes(date, startdateoffset);
            endDate = DateUtils.addMinutes(date, enddateoffset);
        } else if (datematrix.equals(TimeUnit.WEEKLY.getName())) {
            date = c.getTime();
            date = DateUtils.setMilliseconds(date, 0);
            date = DateUtils.setSeconds(date, 0);
            date = DateUtils.setMinutes(date, 0);
            date = DateUtils.setHours(date, 0);
            startDate = DateUtils.addWeeks(date, startdateoffset);
            endDate = DateUtils.addWeeks(date, enddateoffset);
        } else if (datematrix.equals(TimeUnit.MONTHLY.getName())) {
            c.set(Calendar.DAY_OF_MONTH, 1);
            date = c.getTime();
            date = DateUtils.setMilliseconds(date, 0);
            date = DateUtils.setSeconds(date, 0);
            date = DateUtils.setMinutes(date, 0);
            date = DateUtils.setHours(date, 0);
            startDate = DateUtils.addMonths(date, startdateoffset);
            endDate = DateUtils.addMonths(date, enddateoffset);
        }
        timegap.put("$gte", startDate);
        timegap.put("$lt", endDate);
        return timegap;
    }

    private Date getTruncateTimeGap(String starttime, String datematrix, int startdateoffset) {
        Date date;
        try {
            date = this.sdf.parse(starttime);
            if (datematrix.equals(TimeUnit.DAILY.getName().toLowerCase())) {
                date = DateUtils.addDays(date, startdateoffset);
            } else if (datematrix.equals(TimeUnit.HOURLY.getName().toLowerCase())) {
                date = DateUtils.addHours(date, startdateoffset);
            } else if (datematrix.equals(TimeUnit.MINUTELY.getName().toLowerCase())) {
                date = DateUtils.addMinutes(date, startdateoffset);
            } else if (datematrix.equals(TimeUnit.WEEKLY.getName().toLowerCase())) {
                date = DateUtils.addWeeks(date, startdateoffset);
            } else if (datematrix.equals(TimeUnit.MONTHLY.getName().toLowerCase())) {
                date = DateUtils.addMonths(date, startdateoffset);
            }
        } catch (Exception e) {
            s_logger.error("getTruncateTimeGap", e);
            throw new RuntimeException(e.getMessage());
        }
        return date;
    }

    private Date getTruncateBaseDate(String datematrix, int startdateoffset) {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        if (datematrix.toLowerCase().equals(TimeUnit.DAILY.getName().toLowerCase())) {
            date = DateUtils.addDays(date, startdateoffset);
        } else if (datematrix.toLowerCase().equals(TimeUnit.HOURLY.getName().toLowerCase())) {
            date = DateUtils.addHours(date, startdateoffset);
        } else if (datematrix.toLowerCase().equals(TimeUnit.MINUTELY.getName().toLowerCase())) {
            date = DateUtils.addMinutes(date, startdateoffset);
        } else if (datematrix.toLowerCase().equals(TimeUnit.WEEKLY.getName().toLowerCase())) {
            date = DateUtils.addWeeks(date, startdateoffset);
        } else if (datematrix.toLowerCase().equals(TimeUnit.MONTHLY.getName().toLowerCase())) {
            c.set(Calendar.DAY_OF_MONTH, 1);
            date = c.getTime();
            date = DateUtils.addMonths(date, startdateoffset);
        }
        return date;
    }
}
