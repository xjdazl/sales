package com.gome.iuv.dao.mapper;

import com.gome.iuv.dao.entity.ActivatePhoneMessageEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by xjd on 2017/7/3.
 */
public interface ActivatePhoneMessageMapper {

    void batchUpdateHasSubmit(Map map);

    ActivatePhoneMessageEntity findByImei(String imei);

    List<ActivatePhoneMessageEntity> findYesterdayAllActivatePhones(Map map);

    void insert(ActivatePhoneMessageEntity activatePhoneMessageEntity);

    void update(ActivatePhoneMessageEntity activatePhoneMessageEntity);

    List<Map> findMonthSales(Map map);

    List<Map> findDaySales(Map map);
}
