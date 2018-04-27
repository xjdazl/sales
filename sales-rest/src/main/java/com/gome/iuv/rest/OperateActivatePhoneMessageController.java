package com.gome.iuv.rest;

import com.alibaba.fastjson.JSONObject;
import com.gome.iuv.dao.entity.ActivatePhoneMessageEntity;
import com.gome.iuv.dao.entity.SerialImeiMessageEntity;
import com.gome.iuv.dao.enums.CommonEnum;
import com.gome.iuv.domain.ActivatePhoneMsgReqDTO;
import com.gome.iuv.service.ActivatePhoneMessageService;
import com.gome.iuv.service.SerialImeiMessageService;
import com.gome.iuv.utils.AddressBean;
import com.gome.iuv.utils.HttpClientUtil;
import com.gome.iuv.utils.LocationUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;


/**
 * Created by xjd on 2017/7/7.
 */
@Controller
@RequestMapping("/operateActivatePhoneMessage")
public class OperateActivatePhoneMessageController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ActivatePhoneMessageService activatePhoneMessageService;

    @Autowired
    SerialImeiMessageService serialImeiMessageService;

    @RequestMapping(value = "/operate",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject operate(@RequestBody ActivatePhoneMsgReqDTO activatePhoneMsgReqDTO,HttpServletRequest request) {
        logger.info("请求入参："+activatePhoneMsgReqDTO.toString());
        //返回对象
        JSONObject jsonObject = new JSONObject();
        //错误信息
        String ERROR_MESSAGE="XXXXX";
        if((StringUtils.isNotEmpty(activatePhoneMsgReqDTO.getMode()) && activatePhoneMsgReqDTO.getMode().indexOf(ERROR_MESSAGE)>0)
                || (StringUtils.isNotEmpty(activatePhoneMsgReqDTO.getImei()) && activatePhoneMsgReqDTO.getImei().indexOf(ERROR_MESSAGE)>0)
                || (StringUtils.isNotEmpty(activatePhoneMsgReqDTO.getVersion()) && activatePhoneMsgReqDTO.getVersion().indexOf(ERROR_MESSAGE)>0)){

            jsonObject.put("resultcode", "100");
            jsonObject.put("results","empty error");
            logger.info("返回出参："+jsonObject.toString());
            return jsonObject;
        }
        //查询串码表
        SerialImeiMessageEntity serialImeiMessageEntity = serialImeiMessageService.findByImei(activatePhoneMsgReqDTO.getImei());
        //如果串码表中有数据，就去查激活表
        if(serialImeiMessageEntity !=null){
            ActivatePhoneMessageEntity activatePhoneMessageEntity1 = activatePhoneMessageService.findByImei(activatePhoneMsgReqDTO.getImei());
            Date date = new Date();
            //如果手机激活表中没有,就新增一条数据，并且设置第一次激活时间
            if(activatePhoneMessageEntity1 == null){
                //新增激活信息
                opeActivatePhoneMessage( request, activatePhoneMsgReqDTO, ERROR_MESSAGE,1);
            }else{
                //更新标识位，标识CRM数据已经接受过来
                if(activatePhoneMessageEntity1.getIsCrm() ==0){
                    activatePhoneMessageEntity1.setIsCrm(1);
                    activatePhoneMessageEntity1.setUpdateTime(date);
                    activatePhoneMessageEntity1.setSignFirstTime(date);
                    activatePhoneMessageService.update(activatePhoneMessageEntity1);
                    logger.info("--------手机激活信息已存在,更新标识isCrm为1(OperateActivatePhoneMessageController)imei" + activatePhoneMsgReqDTO.getImei() + "--------");
                }else{
                    logger.info("--------手机激活信息已存在(OperateActivatePhoneMessageController)imei" + activatePhoneMsgReqDTO.getImei() + "--------");
                }
            }
        } else {
            ActivatePhoneMessageEntity activatePhoneMessageEntity1 = activatePhoneMessageService.findByImei(activatePhoneMsgReqDTO.getImei());
            //如果手机激活表中没有,就新增一条数据，并且设置第一次激活时间
            if(activatePhoneMessageEntity1 == null){
                //新增激活信息
                opeActivatePhoneMessage( request, activatePhoneMsgReqDTO, ERROR_MESSAGE,0);
                logger.info("--------新增先入库的手机激活信息imei" + activatePhoneMsgReqDTO.getImei() + "--------");
            }
            else{
                //串码表中还没有数据，下次继续发送
                jsonObject.put("resultcode", "100");
                jsonObject.put("results","success");
                logger.info("返回出参："+jsonObject.toString());
                return jsonObject;
            }
        }
        jsonObject.put("resultcode", "200");
        jsonObject.put("results","success");
        logger.info("返回出参："+jsonObject.toString());
        return jsonObject;
    }

    /**
     *操作手机激活信息
     */
    public void opeActivatePhoneMessage(HttpServletRequest request,ActivatePhoneMsgReqDTO activatePhoneMsgReqDTO,String ERROR_MESSAGE,int isCrm){
        Date date = new Date();
        ActivatePhoneMessageEntity activatePhoneMessageEntity1 = new ActivatePhoneMessageEntity();
        activatePhoneMessageEntity1.setSignImei(activatePhoneMsgReqDTO.getImei());
        activatePhoneMessageEntity1.setCreateTime(date);
        activatePhoneMessageEntity1.setIsCrm(isCrm);
        activatePhoneMessageEntity1.setUpdateTime(date);
        activatePhoneMessageEntity1.setSignFirstTime(date);
        activatePhoneMessageEntity1.setHasSubmit(Integer.valueOf(CommonEnum.NO.getIndex()));
        activatePhoneMessageEntity1.setSignMode(Integer.valueOf(activatePhoneMsgReqDTO.getMode()));
        activatePhoneMessageEntity1.setSignBasestation(activatePhoneMsgReqDTO.getBasestation());
        activatePhoneMessageEntity1.setSignVersion(activatePhoneMsgReqDTO.getVersion());
        activatePhoneMessageEntity1.setReceiveBody(activatePhoneMsgReqDTO.toString());
        /**
         * wagnahisheng 2018.2.2 增加手机型号字段
         * 默认为U7
         * */
        activatePhoneMessageEntity1.setPhoneModel(activatePhoneMsgReqDTO.getProduct());
        //gps定位省市
        if(activatePhoneMsgReqDTO.getLocation().indexOf(ERROR_MESSAGE)<0){
            //解析手机的省市信息
            String location = activatePhoneMsgReqDTO.getLocation();
            int latEnd = location.indexOf("LNG");
            String latStr = location.substring(3, latEnd);
            String lngStr = location.substring(latEnd + 3);
            String arr[] = LocationUtil.getAdd(lngStr,latStr);
            if(arr.length>=2){
                activatePhoneMessageEntity1.setProvince(arr[0]);
                activatePhoneMessageEntity1.setCity(arr[1]);
            }else{
                logger.info("--------gps解析省市失败(OperateActivatePhoneMessageController)imei"+activatePhoneMsgReqDTO.getImei()+"--------");
            }
            if(StringUtils.isNotEmpty(activatePhoneMsgReqDTO.getIp()) && activatePhoneMsgReqDTO.getIp().indexOf(ERROR_MESSAGE)<0){
                activatePhoneMessageEntity1.setSignIp(activatePhoneMsgReqDTO.getIp());
            }else{
                activatePhoneMessageEntity1.setSignIp(HttpClientUtil.getIpAddress(request));
            }
            //ip定位省市
        }else if(activatePhoneMsgReqDTO.getIp().indexOf(ERROR_MESSAGE)<0){
            activatePhoneMessageEntity1.setSignIp(activatePhoneMsgReqDTO.getIp());
            //解析手机的省市信息
            try {
                AddressBean addressBean= LocationUtil.getAddresses("ip="+activatePhoneMessageEntity1.getSignIp(), "utf-8");
                if(addressBean != null){
                    activatePhoneMessageEntity1.setProvince(addressBean.getRegion());
                    activatePhoneMessageEntity1.setCity(addressBean.getCity());
                }else{
                    logger.info("--------自解析ip解析省市失败(OperateActivatePhoneMessageController)imei"+activatePhoneMsgReqDTO.getImei()+"--------");
                }
            } catch (Exception e) {
                logger.info("--------自解析ip解析省市异常(OperateActivatePhoneMessageController)imei"+activatePhoneMsgReqDTO.getImei()+"--------");
                e.printStackTrace();
            }
            //自解析ip获取省市
        }else if(activatePhoneMsgReqDTO.getLocation().indexOf(ERROR_MESSAGE)>=0 && activatePhoneMsgReqDTO.getIp().indexOf(ERROR_MESSAGE)>=0){
            activatePhoneMessageEntity1.setSignIp(HttpClientUtil.getIpAddress(request));
            if(StringUtils.isNotEmpty(activatePhoneMessageEntity1.getSignIp())){
                //解析手机的省市信息
                try {
                    AddressBean addressBean= LocationUtil.getAddresses("ip="+activatePhoneMessageEntity1.getSignIp(), "utf-8");
                    if(addressBean != null){
                        activatePhoneMessageEntity1.setProvince(addressBean.getRegion());
                        activatePhoneMessageEntity1.setCity(addressBean.getCity());
                    }else{
                        logger.info("--------自解析ip解析省市失败(OperateActivatePhoneMessageController)imei"+activatePhoneMsgReqDTO.getImei()+"--------");
                    }
                } catch (Exception e) {
                    logger.info("--------自解析ip解析省市异常(OperateActivatePhoneMessageController)imei"+activatePhoneMsgReqDTO.getImei()+"--------");
                    e.printStackTrace();
                }
            }
        }
        else{
            logger.info("--------解析省市失败(OperateActivatePhoneMessageController)imei" + activatePhoneMsgReqDTO.getImei() + "--------");
        }
        //保存激活手机信息
        activatePhoneMessageService.saveActivatePhoneEntity(activatePhoneMessageEntity1);
        logger.info("--------新增激活手机信息成功(OperateActivatePhoneMessageController)imei"+activatePhoneMsgReqDTO.getImei()+"--------");
    }


}
