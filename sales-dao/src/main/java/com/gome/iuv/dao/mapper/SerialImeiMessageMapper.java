package com.gome.iuv.dao.mapper;

import com.gome.iuv.dao.entity.SerialImeiMessageEntity;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;

/**
 * Created by xjd on 2017/7/3.
 */
public interface SerialImeiMessageMapper {

    void insertByBatch(List list) throws DataAccessException;

    SerialImeiMessageEntity findByImei(String imei);

    List<String> findListByImei(Map map);
}
