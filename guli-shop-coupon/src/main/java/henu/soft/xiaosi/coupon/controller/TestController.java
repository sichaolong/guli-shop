package henu.soft.xiaosi.coupon.controller;

import henu.soft.common.utils.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope // 动态从配置中心获取配置文件
@RestController
@RequestMapping("test")
public class TestController {


    /*
    // 从properties配置文件直接取数据
    @Value("${coupon.user.name}")
    private String name;

    @Value("${coupon.user.age}")
    private Integer age;


    @RequestMapping("/dynamic-get-properties")
    public R dynamicGetPropertiesContent(){

        return R.ok().put("name",name).put("age",age);
    }


     */
}
