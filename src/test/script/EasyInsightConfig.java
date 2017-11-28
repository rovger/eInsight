package script;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eInsight.common.EasyInsightConfigInterface;
import com.eInsight.common.utils.EmptyUtil;
import com.eInsight.task.common.TaskTemplate;
import com.eInsight.task.common.TimeUnit;
import com.eInsight.task.common.ValueMappingCallBack;
import com.eInsight.task.mongoCountTask.MongoMapReduceTask;
import com.eInsight.task.mongoCountTask.MongoTemplateCountByPathTask;
import com.eInsight.task.pullTask.PullDataTask;
import com.eInsight.task.reportAlertTask.ReportAlertCallBack;
import com.eInsight.task.reportAlertTask.ReportAlertTask;
import com.eInsight.task.reportAlertTask.ReportEmailAlertTask;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class EasyInsightConfig implements EasyInsightConfigInterface {

    private static String g_dollar = "$";
    private static List<String> opNames = new ArrayList();

    static {
        //B2C or C2C
        opNames.add("SETUP_APM_C2C");
        opNames.add("SETUP_APM_B2C");
    }

    public Map<String, String> getCoreConfig() {
        Map<String, String> coreconfig = new HashMap();
        coreconfig.put("DOMAIN", "eInsight");
        coreconfig.put("EVENT_COLLECTIONNAME", "eInsightEvent");
        return coreconfig;
    }

    public List<TaskTemplate> getTasks() {
        List<TaskTemplate> taskList = new ArrayList();
        taskList.add(new MongoTemplateCountByPathTask("PAS_FlowName", TimeUnit.MINUTELY, 15 * 60 * 1000L, "content.flowName"));
        taskList.add(new MongoTemplateCountByPathTask("PAS_FlowName", TimeUnit.HOURLY));
        taskList.add(new MongoTemplateCountByPathTask("PAS_FlowName", TimeUnit.DAILY));

        taskList.add(new MongoTemplateCountByPathTask("PAS_operationName", TimeUnit.MINUTELY, 15 * 60 * 1000L, "content.operationName"));
        taskList.add(new MongoTemplateCountByPathTask("PAS_operationName", TimeUnit.HOURLY));
        taskList.add(new MongoTemplateCountByPathTask("PAS_operationName", TimeUnit.DAILY));

        String setupmap = "function() {" +
                "var outbound = new String(this.content.outboundData); var payType; " +
                "if((this.content.state=='INITIATED') && (this.content.operationName =='SETUP_APM_C2C' || " +
                "this.content.operationName=='SETUP_APM_B2C')) payType = 'APMSetupFlow_Initialized'; " +
                "if(outbound && outbound.indexOf('payment_type')>=0 && this.content.state=='INSTRUMENT_CONFIRMED'){ " +
                "if(outbound.indexOf('CreditCard')>=0) payType = 'APMSetupFlow_CCSuccess'; " +
                "if(outbound.indexOf('PayPalBA')>=0) payType = 'APMSetupFlow_PPSuccess'; " +
                "if(outbound.indexOf('BankAccount')>=0) payType = 'APMSetupFlow_DDSuccess'; } " +
                "if(payType) emit(payType, {count:1});}";
        String reduce = "function(key, values) { " +
                "var total = 0; " +
                "for (var i = 0; i < values.length; i++) {   " +
                "total += values[i].count;" +
                "}" +
                "return {count:total}; }";
        ValueMappingCallBack apmSetupCallback = new ValueMappingCallBack() {
            public String callback(List<String> countByList) {
                if (countByList == null || countByList.size() == 0) return null;
                String originalValue = countByList.get(0);
                return originalValue;
            }

            public List<String> reverseCallback(String callbackValue) {
                List<String> originalValueList = new ArrayList();
                Map<String, Object> condition = new HashMap();
                if (callbackValue.equals("APMSetupFlow_Initialized")) {
                    condition.put("content.state", "INITIATED");
                    condition.put("content.operationName", new BasicDBObject(g_dollar + "in", opNames.toArray()));

                } else if (callbackValue.equals("APMSetupFlow_CCSuccess")) {
                    DBObject ccReg = new BasicDBObject(g_dollar + "regex", ".*\"payment_type\":\"CreditCard\".*");
                    condition.put("content.outboundData", ccReg);

                } else if (callbackValue.equals("APMSetupFlow_PPSuccess")) {
                    DBObject ppReg = new BasicDBObject(g_dollar + "regex", ".*\"payment_type\":\"PayPalBA\".*");
                    condition.put("content.outboundData", ppReg);

                } else if (callbackValue.equals("APMSetupFlow_DDSuccess")) {
                    DBObject ddReg = new BasicDBObject(g_dollar + "regex", ".*\"payment_type\":\"BankAccount\".*");
                    condition.put("content.outboundData", ddReg);
                }
                originalValueList.add(new Gson().toJson(condition));
                return originalValueList;
            }
        };
        Map<String, Object> allssrirbMap = new HashMap();
        allssrirbMap.put("content.statusCode", "200");
        taskList.add(new MongoMapReduceTask("APMSetupAllFlow", TimeUnit.MINUTELY, apmSetupCallback, allssrirbMap, setupmap, reduce, 15 * 60 * 1000L));
        taskList.add(new MongoTemplateCountByPathTask("APMSetupAllFlow", TimeUnit.HOURLY, 1 * 60 * 60 * 1000L));
        taskList.add(new MongoTemplateCountByPathTask("APMSetupAllFlow", TimeUnit.DAILY, 24 * 60 * 60 * 1000L));

        Map<String, Object> c2cMap = new HashMap();
        c2cMap.put("content.flowName", "SETUP_APM_C2C");
        c2cMap.put("content.statusCode", "200");
        taskList.add(new MongoMapReduceTask("APMSetupSubFlow_C2C", TimeUnit.MINUTELY, apmSetupCallback, c2cMap, setupmap, reduce, 15 * 60 * 1000L));
        taskList.add(new MongoTemplateCountByPathTask("APMSetupSubFlow_C2C", TimeUnit.HOURLY, 1 * 60 * 60 * 1000L));
        taskList.add(new MongoTemplateCountByPathTask("APMSetupSubFlow_C2C", TimeUnit.DAILY, 24 * 60 * 60 * 1000L));

        Map<String, Object> b2cMap = new HashMap();
        b2cMap.put("content.flowName", "SETUP_APM_B2C");
        b2cMap.put("content.statusCode", "200");
        taskList.add(new MongoMapReduceTask("APMSetupSubFlow_B2C", TimeUnit.MINUTELY, apmSetupCallback, b2cMap, setupmap, reduce, 15 * 60 * 1000L));
        taskList.add(new MongoTemplateCountByPathTask("APMSetupSubFlow_B2C", TimeUnit.HOURLY, 1 * 60 * 60 * 1000L));
        taskList.add(new MongoTemplateCountByPathTask("APMSetupSubFlow_B2C", TimeUnit.DAILY, 24 * 60 * 60 * 1000L));

        return taskList;
    }

    public List<TaskTemplate> getBackEndTasks() {
        List<TaskTemplate> taskList = new ArrayList();
        //count alert tasks
        final String baseurl = "http://localhost:8080";
        Map<String, Long> countAlertMap = new HashMap();
        countAlertMap.put("PAS_FlowName:SETUP_APM_C2C:HOURLY:<", 1L);
        for (String reportNameValueStr : countAlertMap.keySet()) {
            final String countAlartReportName = reportNameValueStr;
            final String[] reportNameValue = reportNameValueStr.split(":");
            final Long reportValue = countAlertMap.get(reportNameValueStr);
            ReportAlertCallBack countAlertCallBack = new ReportAlertCallBack() {
                public void init() {
                    try {
                        ReportAlertCallBack.class.getField("alertRule").set(this, countAlartReportName);
                        ReportAlertCallBack.class.getField("alertThreshold").set(this, "" + reportValue);
                        ReportAlertCallBack.class.getField("alertType").set(this, "Count");
                    } catch (Exception e) {
                    }
                }

                public String alert(ReportAlertTask reportAlertTask) {
                    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    StringBuilder content = new StringBuilder("Alert Rule");
                    content.append("<br/>If  " + countAlartReportName + " " + reportValue + " , then alert.<br/>");
                    content.append("<br/>The Current date is " + sdf.format(reportAlertTask.getStartDate()) + " - " + sdf.format(reportAlertTask.getEndDate()) + "<br/>");
                    if (reportAlertTask.isAbnormal()) {
                        content.append("<br/>the current count is abnormal. <br/>");
                    } else {
                        content.append("<br/> the current count back to normal.");
                    }
                    Object targetObj = reportAlertTask.getReportData().get(reportNameValue[1]);
                    String currentCount = "0";
                    if (targetObj != null) currentCount = targetObj.toString();
                    content.append("<br/> current count is " + currentCount);
                    content.append("<br/> threshod count is " + reportValue);
                    content.append("<br/><a href='" + baseurl + "/views/reporting?reportName=" + reportNameValue[0] + "'> click me to open  easyinsight reporting  </a>!");
                    content.append("<br/><div style='text-align:center; color:green'>============================= Don't worried, For Demo Testing! =============================</div>");

                    return content.toString();
                }

                @Override
                public boolean isAbnormalData(ReportAlertTask reportAlertTask) {
                    Map<String, String> reportData = reportAlertTask.getReportData();
                    BigDecimal target = new BigDecimal("0");
                    if (reportData.size()!=0 && !EmptyUtil.isNullOrEmpty(reportData.get(reportNameValue[1]))) {
                        target = new BigDecimal(reportData.get(reportNameValue[1]));
                    }
                    if (reportNameValue[3].contains(">")) {
                        if (target.longValue() > reportValue) {
                            return true;
                        } else {
                            return false;
                        }
                    } else if (reportNameValue[3].contains("<")) {
                        if (target.longValue() < reportValue) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return true;
                    }
                }
            };
            taskList.add(new ReportEmailAlertTask("ReportCountAlertTask::" + reportNameValue[0] + "::" + reportNameValue[1], TimeUnit.valueOf(reportNameValue[2]),
                    reportNameValue[0], reportNameValue[1], countAlertCallBack, "Rovger@163.com", "weijlu@ebay.com,lixie@ebay.com"));
        }

        //percent alert tasks
        Map<String, Double> percentAlertMap = new HashMap();
        percentAlertMap.put("APMSetupAllFlow:APMSetupFlow_Initialized:DAILY:>", 0.75);
        for (String reportNameValueStr2 : percentAlertMap.keySet()) {
            final String percentAlertReportName = reportNameValueStr2;
            final String[] reportNameValue = reportNameValueStr2.split(":");
            final Double reportTargetValue = percentAlertMap.get(reportNameValueStr2);
            ReportAlertCallBack percentAlertCallBack = new ReportAlertCallBack() {
                public void init() {
                    try {
                        ReportAlertCallBack.class.getField("alertRule").set(this, percentAlertReportName);
                        ReportAlertCallBack.class.getField("alertThreshold").set(this, "" + reportTargetValue);
                        ReportAlertCallBack.class.getField("alertType").set(this, "Percent");
                    } catch (Exception e) {
                    }
                }

                @Override
                public String alert(ReportAlertTask reportAlertTask) {
                    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    StringBuilder content = new StringBuilder("Alert Rule");
                    content.append("<br/>If  " + percentAlertReportName + " " + reportTargetValue * 100 + "% , then alert.<br/>");
                    content.append("<br/>The Current date is " + sdf.format(reportAlertTask.getStartDate()) + " - " + sdf.format(reportAlertTask.getEndDate()) + "<br/>");
                    if (reportAlertTask.isAbnormal()) {
                        content.append("<br/>the current percentage is abnormal. <br/>");
                    } else {
                        content.append("<br/> the current percentage is back to normal.");
                    }

                    BigDecimal total = new BigDecimal("0");
                    BigDecimal target = new BigDecimal("0");
                    for (String record : reportAlertTask.getReportData().keySet()) {
                        total = total.add(new BigDecimal(reportAlertTask.getReportData().get(record)));
                    }
                    Object targetObj = reportAlertTask.getReportData().get(reportNameValue[1]);
                    if (targetObj != null) {
                        target = new BigDecimal(targetObj.toString());
                    }
                    String percentage = "0";
                    if (total.longValue() != 0L && target.longValue() != 0L) {
                        double temp = target.divide(total, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
                        percentage = String.valueOf(temp * 100) + "%";
                    }
                    content.append("<br/> current percentage is " + percentage);
                    content.append("<br/> the threshod percentage is " + reportTargetValue * 100 + "%");
                    content.append("<br/><a href='" + baseurl + "/views/reporting?reportName=" + reportNameValue[0] + "'> click me to open  easyinsight reporting " + reportNameValue[0] + " </a>!");
                    content.append("<br/><div style='text-align:center; color:green'>============================= Don't worried, For Demo Testing! =============================</div>");
                    return content.toString();
                }

                @Override
                public boolean isAbnormalData(ReportAlertTask reportAlertTask) {
                    Long total = 0L;
                    Long target = 0L;
                    for (String record : reportAlertTask.getReportData().keySet()) {
                        total = total + Long.parseLong(reportAlertTask.getReportData().get(record));
                    }
                    Object targetObj = reportAlertTask.getReportData().get(reportNameValue[1]);
                    if (targetObj != null) {
                        target = Long.parseLong(targetObj.toString());
                    }

                    if (reportNameValue[3].contains(">")) {
                        if (total != 0L && target / total > reportTargetValue) {
                            return true;
                        } else {
                            return false;
                        }
                    } else if (reportNameValue[3].contains("<")) {
                        if (total != 0L && target / total < reportTargetValue) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return true;
                    }
                }
            };
            taskList.add(new ReportEmailAlertTask("ReportPercentAlertTask::" + reportNameValue[0] + "::" + reportNameValue[1], TimeUnit.valueOf(reportNameValue[2]),
                    reportNameValue[0], reportNameValue[1], percentAlertCallBack, "weijlu@ebay.com", "weijlu@ebay.com,lixie@ebay.com"));
        }

        //pull data task
        taskList.add(new PullDataTask("pulldatatask", TimeUnit.MINUTELY, "http://mmpmsvc-2.stratus.qa.ebay.com/mmpm/audit/pas/{0}/{1}/all"));
        return taskList;
    }

}
