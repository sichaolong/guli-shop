package henu.soft.xiaosi.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("henu.soft.xiaosi.cart.feign")
@EnableRedisHttpSession
public class GuliShopCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliShopCartApplication.class, args);
    }

}
