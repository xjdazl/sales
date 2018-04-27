package com.gome.iuv.service;

import com.gome.iuv.dao.entity.SerialImeiMessageEntity;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;

/**
 * Created by xjd on 2017/7/5.
 */
public interface SerialImeiMessageService {

    /**
     * 保存串行码数据
     * @param list
     */
    void saveSerialImeiMessageList(List list) throws DataAccessException;

    /**
     * 查询单个实体
     * @param imei
     * @return
     */
    SerialImeiMessageEntity findByImei(String imei);

    /**
     * 查询多个
     * @param map
     * @return
     */
    List<String> findListByImei(Map map);
}
