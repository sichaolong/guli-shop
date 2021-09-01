package henu.soft.xiaosi.thirdparty.component.tencent;

import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import henu.soft.common.utils.R;
import henu.soft.xiaosi.thirdparty.config.SmsConfig;
import henu.soft.xiaosi.thirdparty.util.SmsUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 腾讯云短信服务，发送验证码的组件
 */

@Component
public class SendSmsCode {

    @Resource
    SmsConfig smsConfig;

    public R sendSmsCode(String phoneNumber, String code) {
        // 下发手机号码，采用e.164标准，+[国家或地区码][手机号]
        String[] phoneNumbers = {"+86" + phoneNumber};
        // 生成6位随机数字字符串

        // 模板参数：若无模板参数，则设置为空（参数1为随机验证码，参数2为有效时间）
        String[] templateParams = {code};
        // 发送短信验证码

        SendStatus[] sendStatuses = SmsUtil.sendSms(smsConfig, templateParams, phoneNumbers);
        if ("Ok".equals(sendStatuses[0].getCode())) {

            return R.ok();
        } else {
            return R.error(sendStatuses[0].getMessage());
        }
    }
}
