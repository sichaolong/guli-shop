package henu.soft.xiaosi.thirdparty.component.aliyun;


import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * 旧版SDK
 */

//@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
//@Data
//@Component
public class SendSms2 {

    private String host;
    private String path;
    private String skin;
    private String sign;
    private String appcode;

    public void  sendCode(String phone, String code) {
        String method = "GET";
        Map<String, String> headers = new HashMap<>();
        // 最后在header中的格式(中间是英文空格)为 Authorization:APPCODE 93b7e19861a24c519a7548b17dc16d75
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> queries = new HashMap<String, String>();
        queries.put("code", code);
        queries.put("phone", phone);
        queries.put("skin", skin);
        queries.put("sign", sign);
        //JDK 1.8示例代码请在这里下载：  http://code.fegine.com/Tools.zip
        try {
            //HttpResponse response = HttpUtils.doGet(host, path, method, headers, queries);
            //System.out.println(response.toString());如不输出json, 请打开这行代码，打印调试头部状态码。
            //状态码: 200 正常；400 URL无效；401 appCode错误； 403 次数用完； 500 API网管错误
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SendSms2 sendSms2 = new SendSms2();
        sendSms2.sendCode("17637821720","xiaosi");
    }
}
