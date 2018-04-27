package com.gome.iuv.dao.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by xjd on 2017/7/3
 */
public class SerialImeiMessageEntity implements Serializable {


    /**
     * 生成的serialVersionUID
     */
    private static final long serialVersionUID = 5231134212346077681L;

    //imei
    private String signImei;

    //创建时间
    private Date createTime;

    //更新时间
    private Date updateTime;

    public String getSignImei() {
        return signImei;
    }

    public void setSignImei(String signImei) {
        this.signImei = signImei;
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


}
