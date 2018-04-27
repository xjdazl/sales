package com.gome.iuv.service.impl;

import com.gome.iuv.dao.entity.ActivatePhoneMessageEntity;
import com.gome.iuv.dao.mapper.ActivatePhoneMessageMapper;
import com.gome.iuv.dao.mapper.SerialImeiMessageMapper;
import com.gome.iuv.service.ActivatePhoneMessageService;
import com.gome.iuv.service.SerialImeiMessageService;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xjd on 2017/7/5.
 */
@Service
@Transactional
public class ActivatePhoneMessageServiceImpl implements ActivatePhoneMessageService {

    @Autowired
    ActivatePhoneMessageMapper activatePhoneMessageMapper;

    @Override
    public List<ActivatePhoneMessageEntity> findYesterdayAllActivatePhones(Map map) {
        List<ActivatePhoneMessageEntity> list = activatePhoneMessageMapper.findYesterdayAllActivatePhones(map);
        return list;
    }

    @Override
    public void batchUpdateHasSubmit(Map map) {
        activatePhoneMessageMapper.batchUpdateHasSubmit(map);
    }

    @Override
    public ActivatePhoneMessageEntity findByImei(String imei) {
        return activatePhoneMessageMapper.findByImei(imei);
    }

    @Override
    public void saveActivatePhoneEntity(ActivatePhoneMessageEntity activatePhoneMessageEntity) {
        activatePhoneMessageMapper.insert(activatePhoneMessageEntity);
    }

    @Override
    public void update(ActivatePhoneMessageEntity activatePhoneMessageEntity) {
        activatePhoneMessageMapper.update(activatePhoneMessageEntity);
    }

    @Override
    public List<Map> findSales(Map map,boolean isMonth) {
        //查询月销量的返回结果
        List<Map> returnList = new ArrayList<>();
        if(isMonth){
            returnList = activatePhoneMessageMapper.findMonthSales(map);
        }else{
            returnList = activatePhoneMessageMapper.findDaySales(map);
        }
        for(Map returnMap:returnList){
            if(!returnMap.containsKey("imei")){
                Long count = (Long) returnMap.get("count");
                count--;
                returnMap.put("count",count);
            }
        }
        return returnList;
    }

    @Override
    public void statisticsActivateMessage() {
        String fileName = "D:\\副本123.xlsx";
        try {
            FileInputStream fis=new FileInputStream(fileName);
            // 创建Excel工作薄
            Workbook work = null;
            String fileType = fileName.substring(fileName.lastIndexOf("."));
            work = new XSSFWorkbook(fileName);
            Sheet sheet = null;
            Row row = null;
            Cell cell = null;

            // 遍历Excel中所有的sheet
            for (int i = 0; i < work.getNumberOfSheets(); i++) {
                sheet = work.getSheetAt(i);
                if (sheet == null) {
                    continue;
                }
                //已激活
                int hasCommit=0;
                //未激活
                int noCommit=0;
                // 遍历当前sheet中的所有行
                for (int j = sheet.getFirstRowNum(); j < sheet.getLastRowNum() + 1; j++) { // 这里的加一是因为下面的循环跳过取第一行表头的数据内容了
                    row = sheet.getRow(j);
                    if (row == null || row.getFirstCellNum() == j) {
                        continue;
                    }

                    if(j==sheet.getLastRowNum()){
                        cell = row.getCell(0);
                        cell.setCellValue(hasCommit);
                        cell = row.getCell(1);
                        cell.setCellValue(noCommit);
                        continue;
                    }

                    String serialImei = "";
                    cell = row.getCell(0);
                    serialImei = cell.toString();
                    //查询当前串码数据库里还有
                    ActivatePhoneMessageEntity activatePhoneMessageEntity = activatePhoneMessageMapper.findByImei(serialImei);
                    if(activatePhoneMessageEntity != null){
                        cell = row.getCell(1);
                        cell.setCellValue("是");
                        hasCommit++;
                    }else {
                        cell = row.getCell(1);
                        cell.setCellValue("否");
                        noCommit++;
                    }
                }
                //已激活
                hasCommit=0;
                //未激活
                noCommit=0;
            }
                //将修改后的文件写出到D:\\excel目录下
                FileOutputStream os = null;
                os = new FileOutputStream("D:\\新.xlsx");
                os.flush();
                //将Excel写出
                work.write(os);
                fis.close();
                os.close();
                work.close();
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }
}
