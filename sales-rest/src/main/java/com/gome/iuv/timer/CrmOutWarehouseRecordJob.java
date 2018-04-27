package com.gome.iuv.timer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gome.iuv.bean.CrmBean;
import com.gome.iuv.service.SerialImeiMessageService;
import com.gome.iuv.utils.HttpClientUtil;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by xjd on 2017/7/4
 * 查询crm的出库记录，每半点查询一次
 */
@Component
public class CrmOutWarehouseRecordJob {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SerialImeiMessageService serialImeiMessageService;

    @Autowired
    CrmBean crmBean;

    public void execute(){
            logger.info("--------定时器开始(CrmOutWarehouseRecordJob)--------");
            //拼装请求参数
            Map map = new HashMap<String,String>();
            map.put("accessTokenUrl", crmBean.getAccess_token_url());
            map.put("grantType",crmBean.getGrant_type());
            map.put("clientId",crmBean.getClient_id());
            map.put("clientSecret",crmBean.getClient_secret());
            map.put("username",crmBean.getUsername());
            map.put("password",crmBean.getPassword());
           logger.info("--------access_token_url22222--------"+crmBean.getAccess_token_url());
            //获取accessToken
            String accessToken = HttpClientUtil.getAccessToken(map);
            //拼装报文查询crm的出库记录
            if(StringUtils.isNotEmpty(accessToken)){
                    logger.info("--------获取凭证成功(CrmOutWarehouseRecordJob)--------");
                    map.clear();
                    map.put("accessToken", accessToken);
                    map.put("url",crmBean.getOut_warehouse_url());
                    //拼装json格式的请求体
                    JSONObject jObject = new JSONObject();
                    jObject.put("Request_id",crmBean.getRequest_id());
                    JSONObject fJsonObject = new JSONObject();
                    fJsonObject.put("requestPara",jObject);
                    //查询出库记录
                    String outWarehouseRecordStr = HttpClientUtil.sendCrmMessage(map, fJsonObject.toString());
                    if(StringUtils.isNotEmpty(outWarehouseRecordStr) && outWarehouseRecordStr.indexOf("errorCode")>=0){
                        logger.info("--------请求crm信息失败(CrmOutWarehouseRecordJob)--------"+outWarehouseRecordStr);
                        return;
                    }
                    JSONObject jsonObject = JSON.parseObject(outWarehouseRecordStr);
                    //返回成功
                    if(jsonObject!=null && jsonObject.containsKey("Success") && "true".equals(jsonObject.get("Success").toString())){
                      logger.info("--------获取crm返回信息成功(CrmOutWarehouseRecordJob)--------");
                      //解析报文，插入到数据库的串行码表中去
                      JSONArray ja = (JSONArray) jsonObject.get("OutResult");
                      List imeiList = new ArrayList<Map<String,String>>();
                      //定义此list的目的是出现重复IMEI号的时候去重
                      List imeiStrList = new ArrayList<String>();
                      Map outIdMap = new HashMap<String,String>();
                      if(ja.size()>0){
                          logger.info("------ja--"+ja.toString()+"--------");
                          //回传
                          JSONArray returnJsonArray = new JSONArray();
                          Date date = new Date();
                          for(int i=0;i<ja.size();i++){
                                  Map imeiMap = new HashMap<String,String>();
                                  //回传
                                  JSONObject returnJsonObject = new JSONObject();
                                  JSONObject imeiJo = (JSONObject) ja.get(i);
                                  returnJsonObject.put("Request_Id", imeiJo.get("CRMOutId"));
                                  returnJsonArray.add(returnJsonObject);
                                  imeiMap.put("signImei", imeiJo.get("IMEI"));
                                  imeiMap.put("createTime",date);
                                  imeiMap.put("updateTime",date);
                                  imeiList.add(imeiMap);
                                  imeiStrList.add(imeiJo.get("IMEI"));
                                  outIdMap.put(imeiJo.get("IMEI"),imeiJo.get("CRMOutId"));
                          }
                          //保存imei
                          try {
                              serialImeiMessageService.saveSerialImeiMessageList(imeiList);
                              //发送成功信息给crm
                              sendReturnMessage(returnJsonArray,accessToken);
                          } catch (DataAccessException e) {
                              returnJsonArray.clear();
                              //有重复的串码号，去重复
                              Map imeiMap1 = new HashMap<String,String>();
                              imeiMap1.put("signImeis",imeiStrList);
                              List<String> repeatImeiList = serialImeiMessageService.findListByImei(imeiMap1);
                              if(repeatImeiList != null && repeatImeiList.size()>0){
                                  logger.info("--------有重复的串码，重新拼装crm(CrmOutWarehouseRecordJob)--------");
                                  //去除重复的imei
                                  imeiStrList.removeAll(repeatImeiList);
                                  imeiList.clear();
                                  //重复的数据回传标识
                                  for(int j=0;j<repeatImeiList.size();j++){
                                      //回传
                                      JSONObject returnJsonObject = new JSONObject();
                                      returnJsonObject.put("Request_Id", outIdMap.get(repeatImeiList.get(j)));
                                      returnJsonArray.add(returnJsonObject);
                                  }
                                  //不重复的数据处理和回传标识
                                  for(int i=0;i<imeiStrList.size();i++){
                                      Map imeiMap2 = new HashMap<String,String>();
                                      //回传
                                      JSONObject returnJsonObject = new JSONObject();
                                      returnJsonObject.put("Request_Id", outIdMap.get(imeiStrList.get(i)));
                                      returnJsonArray.add(returnJsonObject);
                                      imeiMap2.put("signImei", imeiStrList.get(i));
                                      imeiMap2.put("createTime",date);
                                      imeiMap2.put("updateTime", date);
                                      imeiList.add(imeiMap2);
                                  }
                                  try {
                                      if(imeiList != null && imeiList.size()>0){
                                          //保存imei
                                          serialImeiMessageService.saveSerialImeiMessageList(imeiList);
                                      }else{
                                          logger.info("--------所有数据全部重复保存失败(CrmOutWarehouseRecordJob)--------");
                                      }
                                      //发送成功信息给crm
                                      sendReturnMessage(returnJsonArray,accessToken);
                                  } catch (DataAccessException e1) {
                                      e1.printStackTrace();
                                  }
                              }
                          }
                      }else{
                          logger.info("--------定时器结束,未获取到crm信息(CrmOutWarehouseRecordJob)--------");
                      }
                    }else{
                        logger.info("--------定时器结束,通信失败(CrmOutWarehouseRecordJob)--------");
                    }
            } else {
                logger.info("--------定时器结束,获取凭证失败(CrmOutWarehouseRecordJob)--------");
            }
    }

    /**
     * 发送成功信息给crm
     * @param returnJsonArray
     * @param accessToken
     */
    public void sendReturnMessage(JSONArray returnJsonArray,String accessToken){
        Map map = new HashMap<>();
        //回传imei给crm系统,拼装报文
        JSONObject returnFJsonObject = new JSONObject();
        returnFJsonObject.put("OutRequest",returnJsonArray);
        JSONObject returnGJsonObject = new JSONObject();
        returnGJsonObject.put("requestPara", returnFJsonObject);
        map.clear();
        map.put("accessToken", accessToken);
        map.put("url", crmBean.getOut_warehouse_back_url());
        //回传信息给crm系统
        HttpClientUtil.sendCrmMessage(map, returnGJsonObject.toString());
        logger.info("--------定时器结束,保存成功回传信息给crm(CrmOutWarehouseRecordJob)--------");
    }
}

