package com.eInsight.controller;

import com.eInsight.common.utils.EmptyUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class InsightController {
    @RequestMapping(value = {"/reporting"}, method = {RequestMethod.GET})
    public ModelAndView reporting(
            @RequestParam(value = "reportName", required = false, defaultValue = "") String reportName,
            @RequestParam(value = "reportTitle", required = false, defaultValue = "") String reportTitle,
            @RequestParam(value = "fromdate", required = false, defaultValue = "") String fromdate,
            @RequestParam(value = "todate", required = false, defaultValue = "") String todate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        ModelMap model = new ModelMap();
        ModelAndView view = new ModelAndView();
        if (EmptyUtil.isNullOrEmpty(reportName)) {
            view.setViewName("reporting");
            view.addObject("model", model);
            return view;
        }
        model.addAttribute("reportName", reportName);
        model.addAttribute("reportTitle", reportTitle == null ? reportName : reportTitle);
        model.addAttribute("currentTime", format.format(new Date()));
        model.addAttribute("from", fromdate);
        model.addAttribute("to", todate);
        view.addObject("model", model);
        view.setViewName("layouts/layout001");
        return view;
    }

    @RequestMapping(value = {"/datatables_common"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView datatables(
            @RequestParam(value = "aadata", required = false, defaultValue = "[[1,2],[2,3]]") String aadata,
            @RequestParam(value = "aacolumns", required = false, defaultValue = "[{'title':'[FILL TITLE HERE]") String aacolumns) {
        ModelMap model = new ModelMap();
        model.addAttribute("aadata", aadata);
        model.addAttribute("aacolumns", aacolumns);
        ModelAndView view = new ModelAndView();
        view.setViewName("components/datatables_common");
        view.addObject("model", model);
        return view;
    }

    @RequestMapping(value = {"/showDatatables"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView showDatatables(
            @RequestParam(value = "url", required = true, defaultValue = "") String url,
            @RequestParam(value = "method", required = true, defaultValue = "get") String method,
            @RequestParam(value = "data", required = true, defaultValue = "") String data) {
        ModelMap model = new ModelMap();
        model.addAttribute("url", url);
        model.addAttribute("method", method);
        model.addAttribute("data", data);
        ModelAndView view = new ModelAndView();
        view.setViewName("components/datatables");
        view.addObject("model", model);
        return view;
    }

    @RequestMapping(value = {"/alert"}, method = {RequestMethod.GET})
    public ModelAndView alertService() {
        ModelAndView view = new ModelAndView();
        view.setViewName("alert");
        return view;
    }

    @RequestMapping(value = {"/highchart_line"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView highchart_line(
            @RequestParam(value = "title", required = false, defaultValue = "[DEFINE TITLE HERE]") String title,
            @RequestParam(value = "subTitle", required = false, defaultValue = "[DEFINE SUB TITLE HERE]") String subTitle,
            @RequestParam(value = "xTickList", required = false, defaultValue = "['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun','Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']") String xTickList,
            @RequestParam(value = "yTitle", required = false, defaultValue = "") String yTitle,
            @RequestParam(value = "seriesTitle", required = false, defaultValue = "seriesTitle") String seriesTitle,
            @RequestParam(value = "seriesDataList", required = true, defaultValue = "[{name:'[FILL TAG HERE]',data:[-0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5],type:'line'}]") String seriesDataList,
            @RequestParam(value = "stacking", required = false, defaultValue = "normal") String stacking,
            @RequestParam(value = "valuedecimals", required = false, defaultValue = "0") int valuedecimals,
            @RequestParam(value = "valueprefix", required = false, defaultValue = "") String valueprefix,
            @RequestParam(value = "valuesuffix", required = false, defaultValue = "") String valuesuffix,
            @RequestParam(value = "tickInterval", required = false, defaultValue = "1") int tickInterval,
            @RequestParam(value = "xAixs", required = false, defaultValue = "{}") String xAixs,
            @RequestParam(value = "matrix", required = false, defaultValue = "hour") String matrix,
            @RequestParam(value = "isRate", required = false, defaultValue = "") String isRate) {
        ModelMap model = new ModelMap();
        model.addAttribute("title", title);
        model.addAttribute("subTitle", subTitle);
        model.addAttribute("xTickList", xTickList);
        model.addAttribute("yTitle", yTitle);
        model.addAttribute("seriesTitle", seriesTitle);
        model.addAttribute("seriesDataList", seriesDataList);
        model.addAttribute("stacking", stacking);
        model.addAttribute("valuedecimals", Integer.valueOf(valuedecimals));
        model.addAttribute("valueprefix", valueprefix);
        model.addAttribute("valuesuffix", "rate".equals(isRate) ? "%" : valuesuffix);
        model.addAttribute("tickInterval", Integer.valueOf(tickInterval));
        model.addAttribute("xAixs", xAixs);
        model.addAttribute("isRate", isRate);
        model.addAttribute("matrix", matrix + "ly");
        ModelAndView view = new ModelAndView();
        view.setViewName("components/highchart_line");
        view.addObject("model", model);
        return view;
    }
}
