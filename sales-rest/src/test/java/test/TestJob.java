//package test;
//
//import com.alibaba.fastjson.JSONObject;
//import com.gome.iuv.bean.CrmBean;
//import com.gome.iuv.utils.*;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.xml.stream.Location;
//import java.io.UnsupportedEncodingException;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by xdlgtt on 2017/7/7.
// */
//@Component
//public class TestJob {
//    @Test
//    public void execute() {
//        Map map = new HashMap<String, String>();
//        map.put("url", "http://localhost:8080/rest/operateActivatePhoneMessage/operate");
//        try {
//            int imei = 99;
//            for(int i=0;i<1000;i++){
//                String jsonObjectStr = DesUtil.encrypt3DES("{\"imei\":\""+imei+"\",\"ip\":\"36.60.161.134\",\"location\":\"XXXXX\",\"phase\":\"1\",\"basestation\":\"sdfg\",\"mode\":\"1\"}");
//                String abc = HttpClientUtil.sendCrmMessage(map, jsonObjectStr);
//                System.out.println(abc);
//                System.out.println(DesUtil.decrypt3DES(abc));
//                imei++;
//                System.out.println(i);
//            }
//            System.out.println("发送完成");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void test123(){
//        try {
//            AddressBean addressBean= LocationUtil.getAddresses("ip=122.96.40.237", "utf-8");
//            String[]  m = LocationUtil.getAddressById("122.96.40.237");
//            System.out.print("123");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
