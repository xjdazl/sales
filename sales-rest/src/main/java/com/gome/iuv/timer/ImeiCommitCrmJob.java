package com.gome.iuv.timer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gome.iuv.bean.CrmBean;
import com.gome.iuv.dao.entity.ActivatePhoneMessageEntity;
import com.gome.iuv.dao.enums.CommonEnum;
import com.gome.iuv.service.ActivatePhoneMessageService;
import com.gome.iuv.service.SerialImeiMessageService;
import com.gome.iuv.utils.HttpClientUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by xjd on 2017/7/4
 * imei提交到crm的3次激活过的数据
 */
@Component
public class ImeiCommitCrmJob {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    //一次提交的最大数量
    private final static int MAX_COMMIT_NUMBER = 100;

    @Autowired
    ActivatePhoneMessageService activatePhoneMessageService;

    @Autowired
    CrmBean crmBean;

    public void execute(){
            logger.info("--------定时器开始(ImeiCommitCrmJob)--------");
            //拼装请求参数
            Map map = new HashMap<String,String>();
            map.put("hasSubmit", CommonEnum.NO.getIndex());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
            String startDate = simpleDateFormat.format(calendar.getTime())+" 00:00:00";
            String endDate = simpleDateFormat.format(calendar.getTime())+" 23:59:59";
            map.put("startDate",startDate);
            map.put("endDate", endDate);
            //获取前一天的所有都激活的手机信息
            List<ActivatePhoneMessageEntity> list = activatePhoneMessageService.findYesterdayAllActivatePhones(map);
            if(list != null && list.size()>0){
                map.clear();
                map.put("accessTokenUrl", crmBean.getAccess_token_url());
                map.put("grantType",crmBean.getGrant_type());
                map.put("clientId",crmBean.getClient_id());
                map.put("clientSecret",crmBean.getClient_secret());
                map.put("username",crmBean.getUsername());
                map.put("password",crmBean.getPassword());
                //获取accessToken
                String accessToken = HttpClientUtil.getAccessToken(map);
                if(StringUtils.isNotEmpty(accessToken)){
                    int listSize = list.size();
                    //循环次数
                    int number = (int)Math.ceil((double)listSize/(double)MAX_COMMIT_NUMBER);
                    for(int m=0;m<number;m++) {
                        //发送激活信息给crm
                        sendCrmActiveMessage(m,1,list,map,accessToken);
                    }
                }else{
                    logger.info("--------定时器结束,获取凭证失败(ImeiCommitCrmJob)--------");
                }
            }else{
                logger.info("--------定时器结束,未查询到前一天的手机激活信息(ImeiCommitCrmJob)--------");
            }
    }

    /**
     *发送给crm激活信息
     */
    public void sendCrmActiveMessage(int m,int n,List<ActivatePhoneMessageEntity> list,Map map,String accessToken){
        logger.info("--------获取凭证成功(ImeiCommitCrmJob)--------");
        JSONObject gJsonObject = new JSONObject();
        JSONObject fJsonObject = new JSONObject();
        JSONArray jArray = new JSONArray();
        //记录imei号，提交成功后更新对应的hasSubmit字段
        Map hasSubmitMap = new HashMap<>();
        hasSubmitMap.put("hasSubmit", CommonEnum.YES.getIndex());
        hasSubmitMap.put("updateTime", new Date());
        List<String> imeiList = new ArrayList<String>();
        //拼装报文,提交到crm的1次激活过的数据
        for(int i=MAX_COMMIT_NUMBER*m;i<list.size();i++){
            JSONObject jObject = new JSONObject();
            imeiList.add(list.get(i).getSignImei());
            jObject.put("sign_imei", list.get(i).getSignImei());
            jObject.put("sign_basestation", list.get(i).getSignBasestation());
            jObject.put("sign_version", list.get(i).getSignVersion());
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            jObject.put("sign_first_time", sdFormat.format(list.get(i).getSignFirstTime()));
            jObject.put("sign_mode", CommonEnum.getName(list.get(i).getSignMode()));
            jObject.put("sign_ip", list.get(i).getSignIp());
            jObject.put("address_str", list.get(i).getProvince() + list.get(i).getCity());
            jArray.add(jObject);
            if(jArray.size()>=MAX_COMMIT_NUMBER){
                break;
            }
        }
        hasSubmitMap.put("signImeis", imeiList);
        fJsonObject.put("paraList", jArray);
        gJsonObject.put("requestPara", fJsonObject);
        //设置请求头参数
        map.put("accessToken", accessToken);
        map.put("url", crmBean.getImei_commit_crm_url());
        //发送请求
        String repStr = HttpClientUtil.sendCrmMessage(map, gJsonObject.toString());
        logger.info("--------crm返回信息" + repStr + "(ImeiCommitCrmJob)--------");
        JSONObject jObject = JSON.parseObject(repStr);
        //返回成功
        if (jObject != null && jObject.containsKey("Success") && "true".equals(jObject.get("Success").toString())) {
            logger.info("--------提交激活手机信息成功(ImeiCommitCrmJob)--------");
            //更新激活手机表中的是否提交的标识为已提交
            activatePhoneMessageService.batchUpdateHasSubmit(hasSubmitMap);
            logger.info("--------定时器结束,更新激活手机信息表成功已提交(ImeiCommitCrmJob)--------");
        } else {
            try {
                if(n<=3){
                    logger.info("--------定时器结束,提交激活手机信息失败，5秒后重新提交(ImeiCommitCrmJob)--------"+n+"次");
                    //延时5秒再执行
                    Thread.sleep(5000);
                    sendCrmActiveMessage(m,n,list,map,accessToken);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

