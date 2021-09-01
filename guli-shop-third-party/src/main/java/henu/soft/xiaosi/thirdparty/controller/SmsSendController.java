package henu.soft.xiaosi.thirdparty.controller;

import henu.soft.common.utils.R;

import henu.soft.xiaosi.thirdparty.component.tencent.SendSmsCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 供其他微服务模块使用短信验证码服务
 */
@RestController
@RequestMapping("/sms")
public class SmsSendController {



    @Autowired
    SendSmsCode sendSmsCode;

    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phone,@RequestParam("code") String code){
        try {
            R r = sendSmsCode.sendSmsCode(phone, code);

            return r;
        } catch (Exception e) {
            e.printStackTrace();
            return R.error();
        }
    }
}
