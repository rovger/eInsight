package com.eInsight.task.reportAlertTask;

import com.eInsight.common.exception.BizExceptionType;
import com.eInsight.common.exception.BizRuntimeException;
import com.eInsight.task.common.SpringContext;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

public class ReportAlertContentBuilder {
    private static final String FREE_MARKER_CONFIGURER = "mongoTemplate";
    private String templatePath;
    private static Logger s_logger = Logger.getLogger(ReportAlertContentBuilder.class);
    private Map<String, Object> dataMap = new HashMap();

    public ReportAlertContentBuilder(String templateFolderName) {
        this.templatePath = (templateFolderName + "/content.ftl");
    }

    public Map<String, Object> getDataMap() {
        return this.dataMap;
    }

    public String buildcontent() {
        Map<String, Object> dataMap = getDataMap();
        return buildXqlByData(dataMap, this.templatePath);
    }

    private String buildXqlByData(Map<String, Object> dataMap, String filepath) {
        Template t;
        try {
            FreeMarkerConfigurer freeMarkerConfigurer = (FreeMarkerConfigurer) SpringContext.getSpringContext().getBean(FREE_MARKER_CONFIGURER);
            t = freeMarkerConfigurer.getConfiguration().getTemplate(filepath);
        } catch (IOException e) {
            s_logger.error(BizExceptionType.TEMPLATE_FILE_NOT_FOUND.getDescription() + ", details: " + e.getMessage());
            throw new BizRuntimeException(BizExceptionType.TEMPLATE_FILE_NOT_FOUND);
        }
        Writer out = new StringWriter();
        try {
            t.process(dataMap, out);
        } catch (TemplateException e) {
            s_logger.error(BizExceptionType.TEMPLATE_PROCESSING_ERROR.getDescription() + ", details: " + e.getMessage());
            throw new BizRuntimeException(BizExceptionType.TEMPLATE_PROCESSING_ERROR);
        } catch (IOException e) {
            s_logger.error(BizExceptionType.TEMPLATE_PROCESSING_IO_ERROR.getDescription() + ", details: " + e.getMessage());
            throw new BizRuntimeException(BizExceptionType.TEMPLATE_PROCESSING_IO_ERROR);
        }
        return out.toString();
    }
}
