package com.gome.iuv.service;

import com.gome.iuv.dao.entity.ActivatePhoneMessageEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by xjd on 2017/7/5.
 */
public interface ActivatePhoneMessageService {

    /**
     * 查询激活手机信息
     * @param map
     * @return
     */
    List<ActivatePhoneMessageEntity> findYesterdayAllActivatePhones(Map map);

    /**
     * 批量更新激活手机信息表
     * @param map
     */
    void batchUpdateHasSubmit(Map map);

    /**
     * 查询单个实体
     * @param imei
     * @return
     */
    ActivatePhoneMessageEntity findByImei(String imei);

    /**
     * 保存手机激活信息
     * @param activatePhoneMessageEntity
     */
    void saveActivatePhoneEntity(ActivatePhoneMessageEntity activatePhoneMessageEntity);

    /**
     * 更新
     * @param activatePhoneMessageEntity
     */
    void update(ActivatePhoneMessageEntity activatePhoneMessageEntity);

    /**
     * 查询销量
     * @param map
     * @return
     */
    List<Map> findSales(Map map,boolean isMonth);

    /**
     * 统计激活信息
     */
    void statisticsActivateMessage();
}
