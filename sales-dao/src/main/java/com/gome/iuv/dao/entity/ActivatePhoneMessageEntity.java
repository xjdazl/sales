package com.gome.iuv.dao.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by xjd on 2017/7/3
 */
public class ActivatePhoneMessageEntity implements Serializable {


    /**
     * 生成的serialVersionUID
     */
    private static final long serialVersionUID = 5231134212346076681L;

    //imei
    private String signImei;

    //内部版本号
    private String signVersion;

    //ip
    private String signIp;

    //基站信息
    private String signBasestation;

    //省
    private String  province;

    //市
    private String city;

    //是否是真实的用户信息
    private Integer signMode;

    //第一次激活时间
    private Date signFirstTime;

    //第二次激活时间（作废）
    private Date  signSecondTime;

    //第三次激活时间（作废）
    private Date signThirdTime;

    //是否已经提交到crm
    private Integer hasSubmit;

    //接受到的报文体
    private String receiveBody;

    //创建时间
    private Date createTime;

    //更新时间
    private Date updateTime;

    //1：是 0：否
    private Integer isCrm;

    //手机模型
    private String phoneModel;

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getSignImei() {
        return signImei;
    }

    public void setSignImei(String signImei) {
        this.signImei = signImei;
    }

    public String getSignVersion() {
        return signVersion;
    }

    public void setSignVersion(String signVersion) {
        this.signVersion = signVersion;
    }

    public String getSignIp() {
        return signIp;
    }

    public void setSignIp(String signIp) {
        this.signIp = signIp;
    }

    public String getSignBasestation() {
        return signBasestation;
    }

    public void setSignBasestation(String signBasestation) {
        this.signBasestation = signBasestation;
    }

    public Integer getSignMode() {
        return signMode;
    }

    public void setSignMode(Integer signMode) {
        this.signMode = signMode;
    }

    public Date getSignFirstTime() {
        return signFirstTime;
    }

    public void setSignFirstTime(Date signFirstTime) {
        this.signFirstTime = signFirstTime;
    }

    public Date getSignSecondTime() {
        return signSecondTime;
    }

    public void setSignSecondTime(Date signSecondTime) {
        this.signSecondTime = signSecondTime;
    }

    public Date getSignThirdTime() {
        return signThirdTime;
    }

    public void setSignThirdTime(Date signThirdTime) {
        this.signThirdTime = signThirdTime;
    }

    public Integer getHasSubmit() {
        return hasSubmit;
    }

    public void setHasSubmit(Integer hasSubmit) {
        this.hasSubmit = hasSubmit;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getReceiveBody() {
        return receiveBody;
    }

    public void setReceiveBody(String receiveBody) {
        this.receiveBody = receiveBody;
    }

    public Integer getIsCrm() {
        return isCrm;
    }

    public void setIsCrm(Integer isCrm) {
        this.isCrm = isCrm;
    }
}
