package com.gome.iuv.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by xjd on 2017/5/8.
 */
public class ActivatePhoneMsgReqDTO implements Serializable{

    private static final long serialVersionUID = -567143346252119591L;

    //imei
    private String imei;

    //内部版本号
    private String version;

    //第几次的报文数据
    private String phase;

    //基站信息
    private String basestation;

    //省市
    private String  location;

    //是否是真实的用户信息
    private String mode;

    //手机ip信息
    private String ip;

    //手机模型
    private String product;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getBasestation() {
        return basestation;
    }

    public void setBasestation(String basestation) {
        this.basestation = basestation;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "ActivatePhoneMsgReqDTO{" +
                "imei='" + imei + '\'' +
                ", version='" + version + '\'' +
                ", phase='" + phase + '\'' +
                ", basestation='" + basestation + '\'' +
                ", location='" + location + '\'' +
                ", mode='" + mode + '\'' +
                ", ip='" + ip + '\'' +
                ", product='" + product + '\'' +
                '}';
    }
}
