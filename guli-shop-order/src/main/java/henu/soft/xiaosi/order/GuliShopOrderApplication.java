package henu.soft.xiaosi.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("henu.soft.xiaosi.order.dao")
@EnableRabbit
@EnableRedisHttpSession
@EnableFeignClients("henu.soft.xiaosi.order.feign")
@EnableTransactionManagement
public class GuliShopOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliShopOrderApplication.class, args);
    }

}
