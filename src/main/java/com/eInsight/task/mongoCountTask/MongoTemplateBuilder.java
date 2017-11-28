package com.eInsight.task.mongoCountTask;

import com.eInsight.common.exception.BizExceptionType;
import com.eInsight.common.exception.BizRuntimeException;
import com.eInsight.task.common.QueryConditionResultObject;
import com.eInsight.task.common.SpringContext;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

public class MongoTemplateBuilder {
    private static Logger s_logger = Logger.getLogger(MongoTemplateBuilder.class);
    private static final String FREE_MARKER_CONFIGURER = "mongoTemplate";
    protected String matchfilepath;
    protected String projectfilepath;
    protected String groupfilepath;
    private Map<String, Object> dataMap = new HashMap();

    public MongoTemplateBuilder(String mongoTemplateName) {
        this.matchfilepath = (mongoTemplateName + "/match.ftl");
        this.projectfilepath = (mongoTemplateName + "/project.ftl");
        this.groupfilepath = (mongoTemplateName + "/group.ftl");
    }

    public Map<String, Object> getDataMap() {
        return this.dataMap;
    }

    public QueryConditionResultObject getQueryConditionObjects() {
        DBObject match = buildMatchFragment(this.matchfilepath);
        DBObject project = buildProjectFragment(this.projectfilepath);
        DBObject group = buildGroupFragment(this.groupfilepath);
        return new QueryConditionResultObject(match, project, group);
    }

    private DBObject buildMatchFragment(String filepath) {
        Map<String, Object> dataMap = getDataMap();
        return buildXqlByData(dataMap, filepath);
    }

    private DBObject buildProjectFragment(String filepath) {
        Map<String, Object> dataMap = getDataMap();
        return buildXqlByData(dataMap, filepath);
    }

    private DBObject buildGroupFragment(String filepath) {
        Map<String, Object> dataMap = getDataMap();
        return buildXqlByData(dataMap, filepath);
    }

    private DBObject buildXqlByData(Map<String, Object> dataMap, String filepath) {
        Template template;
        try {
            FreeMarkerConfigurer freeMarkerConfigurer = (FreeMarkerConfigurer) SpringContext.getSpringContext().getBean(FREE_MARKER_CONFIGURER);
            template = freeMarkerConfigurer.getConfiguration().getTemplate(filepath);
        } catch (IOException ex) {
            s_logger.error(BizExceptionType.TEMPLATE_FILE_NOT_FOUND.getDescription() + ", details: " + ex.getMessage());
            throw new BizRuntimeException(BizExceptionType.TEMPLATE_FILE_NOT_FOUND, ex);
        }
        Writer out = new StringWriter();
        try {
            template.process(dataMap, out);
        } catch (TemplateException ex) {
            s_logger.error(BizExceptionType.TEMPLATE_PROCESSING_ERROR.getDescription() + ", details: " + ex.getMessage());
            throw new BizRuntimeException(BizExceptionType.TEMPLATE_PROCESSING_ERROR, ex);
        } catch (IOException ex) {
            s_logger.error(BizExceptionType.TEMPLATE_PROCESSING_IO_ERROR.getDescription() + ", details: " + ex.getMessage());
            throw new BizRuntimeException(BizExceptionType.TEMPLATE_PROCESSING_IO_ERROR, ex);
        }
        return (DBObject) JSON.parse(out.toString());
    }
}
