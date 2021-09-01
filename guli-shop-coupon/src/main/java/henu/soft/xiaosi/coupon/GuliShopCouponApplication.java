package henu.soft.xiaosi.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("henu.soft.xiaosi.coupon.dao")
@EnableFeignClients // 开启远程调用
public class GuliShopCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliShopCouponApplication.class, args);
    }

}
