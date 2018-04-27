//package test;
//
//import org.apache.poi.hssf.usermodel.HSSFCell;
//import org.apache.poi.hssf.usermodel.HSSFRow;
//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.xssf.usermodel.XSSFCell;
//import org.apache.poi.xssf.usermodel.XSSFRow;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * User: xiajiadong
// * Date: 2018/4/9  11:08
// */
//public class ReadExcel {
//
//    @Test
//    public void test(){
//        try {
//            String sql = "insert into serial_imei_message (sign_imei,create_time,update_time) VALUES";
//            readXls("D:\\01(1).xlsx",sql);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private static String readXls(String path,String sql) throws Exception {
//        InputStream is = new FileInputStream(path);
//        // HSSFWorkbook 标识整个excel
//        XSSFWorkbook xSSFWorkbook = new XSSFWorkbook(is);
//        List<List<String>> result = new ArrayList<List<String>>();
//        int size = xSSFWorkbook.getNumberOfSheets();
//        // 循环每一页，并处理当前循环页
//        for (int numSheet = 0; numSheet < size; numSheet++) {
//            // HSSFSheet 标识某一页
//            XSSFSheet xSSFSheet = xSSFWorkbook.getSheetAt(numSheet);
//            if (xSSFSheet == null) {
//                continue;
//            }
//            // 处理当前页，循环读取每一行
//            for (int rowNum = 1; rowNum <= xSSFSheet.getLastRowNum(); rowNum++) {
//                // HSSFRow表示行
//                XSSFRow xssfRow = xSSFSheet.getRow(rowNum);
//                int minColIx = xssfRow.getFirstCellNum();
//                int maxColIx = xssfRow.getLastCellNum();
//                List<String> rowList = new ArrayList<String>();
//                // 遍历改行，获取处理每个cell元素
//                for (int colIx = minColIx; colIx < maxColIx; colIx++) {
//                    // HSSFCell 表示单元格
//                    XSSFCell cell = xssfRow.getCell(colIx);
//                    if (cell == null) {
//                        continue;
//                    }
//                    sql = sql + "('"+cell.toString()+"',now(),now()),";
//                }
//            }
//        }
//        System.out.println(sql);
//        return sql;
//    }
//
//}
