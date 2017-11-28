package com.eInsight.resources;

import com.eInsight.common.dao.impl.AlertDAOImpl;
import com.eInsight.common.utils.ConvertUtil;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
@Path("/alert")
public class AlertEventService {
    private static final Logger s_logger = Logger.getLogger(AlertEventService.class);
    @Context
    HttpServletRequest servletRequest;

    @GET
    @Path("/{mongopath}/{mongovalue}/{limit}")
    @Produces({"application/json"})
    public String getEventInfoByKeyValueWithLimit(
            @PathParam("mongopath") String mongopath,
            @PathParam("mongovalue") String mongovalue,
            @PathParam("limit") int limit) {
        BasicDBObject condition = new BasicDBObject();
        if ((mongovalue.trim().startsWith("{")) && (mongovalue.trim().endsWith("}"))) {
            HashMap mongoexpress = new Gson().fromJson(mongovalue, HashMap.class);
            condition.put(mongopath, mongoexpress);
        } else {
            condition.put(mongopath, mongovalue);
        }
        return getEventInfoByObject(condition.toString(), limit);
    }

    @GET
    @Path("/find/{condition}/{limit}")
    @Consumes({"application/json"})
    public String getEventInfoByObject(
            @PathParam("condition") String condition,
            @PathParam("limit") int limit) {
        BasicDBObject querycondition = new BasicDBObject(new Gson().fromJson(condition, HashMap.class));
        return findByConditionLimt(limit, querycondition);
    }

    public String findByConditionLimt(int limit, BasicDBObject querycondition) {
        String response;
        try {
            List<DBObject> resultlist = AlertDAOImpl.getAlertDAOInstace().queryListByObjectWithLimitAndSort(querycondition, limit, new Object[]{"CreationDate", Integer.valueOf(-1)});
            response = ConvertUtil.buildResponse(querycondition, resultlist);
        } catch (Exception e) {
            s_logger.error("getEventInfoByObject", e);
            throw new RuntimeException(e);
        }
        return response;
    }
}
