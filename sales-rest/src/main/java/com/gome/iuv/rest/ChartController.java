package com.gome.iuv.rest;

import com.alibaba.fastjson.JSONObject;
import com.gome.iuv.bean.CrmBean;
import com.gome.iuv.service.ActivatePhoneMessageService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by xjd on 2017/7/31.
 */
@Controller
@RequestMapping("/chart")
public class ChartController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ActivatePhoneMessageService activatePhoneMessageService;

    /**
     * 获取图表的销量统计数据
     * @param request
     * @param response
     */
    @RequestMapping(value = "/getSalesChartData",method = RequestMethod.GET)
    public void  getSalesChartData(HttpServletRequest request,HttpServletResponse response){
        PrintWriter print;
        response.setContentType("text/html;charset=utf-8");
        String jsonStr;
        Map map = new HashMap<String,String>();
        //返回的json对象
        JSONObject returnJsonObject = new JSONObject();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            print=response.getWriter();
            String date=request.getParameter("date");
            String province = request.getParameter("province");
            String city = request.getParameter("city");
            if(StringUtils.isNotEmpty(province)){
                province = URLDecoder.decode(province, "utf-8");
                map.put("province",province);
            }
            if(StringUtils.isNotEmpty(city)){
                city = URLDecoder.decode(city, "utf-8");
                map.put("city",city);
            }
            String callback = request.getParameter("callback");
            String year = date.substring(0, date.indexOf("-"));
            map.put("year", year);
            map.put("month",date);
            //月销量统计对比数据
            List<Map> monthSalesList = activatePhoneMessageService.findSales(map, true);
            //日销量统计对比数据
            List<Map> daySalesList = activatePhoneMessageService.findSales(map, false);
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(dateFormat.parse(date + "-20"));
                int dayCounts = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                for(int i=30;i>dayCounts-1;i--){
                    daySalesList.remove(i);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            returnJsonObject.put("monthSales", monthSalesList);
            returnJsonObject.put("daySales", daySalesList);
            String returnStr = returnJsonObject.toString();
            print.write(callback+ "(" + returnStr + ")");
            print.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计销量激活信息
     */
    @RequestMapping(value = "/statisticsActivateMessage",method = RequestMethod.GET)
    public void statisticsActivateMessage(){
        activatePhoneMessageService.statisticsActivateMessage();
    }
}
