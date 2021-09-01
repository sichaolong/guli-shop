package henu.soft.xiaosi.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

@EnableFeignClients(value = "henu.soft.xiaosi.authserver.feign")
@EnableDiscoveryClient
@SpringBootApplication
@EnableRedisHttpSession// 开启分布式session
public class GuliShopAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliShopAuthServerApplication.class, args);
    }

}
