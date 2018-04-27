package com.gome.iuv.service.impl;

import com.gome.iuv.dao.entity.SerialImeiMessageEntity;
import com.gome.iuv.dao.mapper.SerialImeiMessageMapper;
import com.gome.iuv.service.SerialImeiMessageService;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by xjd on 2017/7/5.
 */
@Service
@Transactional
public class SerialImeiMessageServiceImpl implements SerialImeiMessageService {

    @Autowired
    SerialImeiMessageMapper serialImeiMessageMapper;

    @Override
    public void saveSerialImeiMessageList(List list) throws DataAccessException {
        serialImeiMessageMapper.insertByBatch(list);
    }

    @Override
    public SerialImeiMessageEntity findByImei(String imei) {
        return serialImeiMessageMapper.findByImei(imei);
    }

    @Override
    public List<String> findListByImei(Map map) {
        return serialImeiMessageMapper.findListByImei(map);
    }
}
