package henu.soft.xiaosi.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients(value = {"henu.soft.xiaosi.search.feign"})
@EnableRedisHttpSession
public class GuliShopSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliShopSearchApplication.class, args);
    }

}
