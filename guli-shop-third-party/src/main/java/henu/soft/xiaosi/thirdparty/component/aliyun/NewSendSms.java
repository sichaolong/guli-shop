package henu.soft.xiaosi.thirdparty.component.aliyun;

import com.alibaba.fastjson.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;
import henu.soft.common.utils.R;
import org.springframework.stereotype.Component;


/**
 * 阿里云短信新版SDK,封装组件
 */
@Component
public class NewSendSms {
    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    public com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    public R newSdkSendCode(String phone, String code) throws Exception {
        Client client = createClient("","");
        /*
        QuerySendDetailsRequest querySendDetailsRequest = new QuerySendDetailsRequest()
                .setResourceOwnerAccount("1")
                .setResourceOwnerId(1L)
                .setPhoneNumber("17637821720")
                .setBizId("1")
                .setSendDate("xiaosi");
        // 复制代码运行请自行打印 API 的返回值
        client.querySendDetails(querySendDetailsRequest);

        */

        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phone);
        request.setTemplateCode("SMS_222325341");
        request.setTemplateParam(code);
        request.setSignName("垃圾慧分类助手");


        SendSmsResponse sendSmsResponse = client.sendSms(request);

        SendSmsResponseBody body = sendSmsResponse.getBody();
        String s = JSON.toJSONString(body);
        System.out.println(s);

        if (sendSmsResponse.body.code.equals("200") ){

            return R.ok();
        }
        else return R.error("验证码发送失败！");


    }

    public static void main(String[] args) throws Exception {
        NewSendSms newSendSms = new NewSendSms();
        newSendSms.newSdkSendCode("17637821720","111");
    }


}
