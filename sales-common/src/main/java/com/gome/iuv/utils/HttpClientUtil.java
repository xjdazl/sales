package com.gome.iuv.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * Created by xjd on 2017/7/4.
 */
public class HttpClientUtil {

    private final static Logger logger = LoggerFactory.getLogger("com.gome.iuv.utils.HttpClientUtil");

    //超时时间2分钟
    private final static int timeOut = 120000;

    /**
     * 获取令牌
     * @param map
     * @return
     */
      public static String getAccessToken(Map<String,String> map){
          String accessToken = null;
          CloseableHttpClient httpclient = HttpClients.createDefault();
          try {
              HttpPost httppost = new HttpPost(map.get("accessTokenUrl"));
              //拼装请求参数
              String paramValue =
                      "grant_type="+map.get("grantType")+"&" +
                      "client_id="+map.get("clientId")+"&" +
                      "client_secret="+map.get("clientSecret")+"&" +
                      "username="+map.get("username")+"&" +
                      "password="+map.get("password");
              StringEntity StringEntity = new StringEntity(paramValue, ContentType.APPLICATION_FORM_URLENCODED);
              RequestConfig requestConfig = RequestConfig.custom()
                      .setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut)
                      .setSocketTimeout(timeOut).build();
              //设置请求参数
              httppost.setEntity(StringEntity);
              httppost.setConfig(requestConfig);
              CloseableHttpResponse response = null;
              try {
                  //发送请求
                  response = httpclient.execute(httppost);
                  HttpEntity entity = response.getEntity();
                  if(entity != null){
                      String repStr = EntityUtils.toString(response.getEntity(), "utf-8");
                      JSONObject jo = JSON.parseObject(repStr);
                      if(jo.containsKey("access_token")){
                          accessToken = (String) jo.get("access_token");
                          return accessToken;
                      }
                  }
              } catch (IOException e) {
                  logger.error(e.getMessage());
              } finally {
                  try {
                      response.close();
                  } catch (IOException e) {
                      logger.error(e.getMessage());
                  }
              }
          }
          finally {
              try {
                  httpclient.close();
              } catch (IOException e) {
                  logger.error(e.getMessage());
              }
          }
          return accessToken;
      }

    /**
     * 发送报文给crm系统
     * @param map
     * @return
     */
      public static String sendCrmMessage(Map<String,String> map,String jObject){
          String repStr = null;
          CloseableHttpClient httpclient = HttpClients.createDefault();
          try {
              HttpPost httppost = new HttpPost(map.get("url"));
              //设置令牌
              httppost.setHeader("Authorization", "Bearer "+map.get("accessToken"));
              httppost.setHeader("content-type", "application/json");
              StringEntity StringEntity = new StringEntity(jObject, ContentType.APPLICATION_JSON);
              RequestConfig requestConfig = RequestConfig.custom()
                      .setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut)
                      .setSocketTimeout(timeOut).build();
              //设置参数
              httppost.setConfig(requestConfig);
              httppost.setEntity(StringEntity);

              CloseableHttpResponse response = null;
              try {
                  //发送请求
                  response = httpclient.execute(httppost);
                  HttpEntity entity = response.getEntity();
                  if(entity != null){
                      repStr = EntityUtils.toString(response.getEntity(), "utf-8");
                  }
              } catch (IOException e) {
                  logger.error(e.getMessage());
              } finally {
                  try {
                      response.close();
                  } catch (IOException e) {
                      logger.error(e.getMessage());
                  }
              }
          }
          finally {
              try {
                  httpclient.close();
              } catch (IOException e) {
                  logger.error(e.getMessage());
              }
          }
          return repStr;
      }

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
