package henu.soft.xiaosi.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableCaching// 开启缓存
@EnableFeignClients(basePackages = "henu.soft.xiaosi.product.feign") // 远程调用
@SpringBootApplication
@EnableDiscoveryClient // 注册中心
@EnableTransactionManagement // 开启事务
@EnableRedisHttpSession//开启分布式缓存
@MapperScan("henu.soft.xiaosi.product.dao")
public class GuliShopProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliShopProductApplication.class, args);
    }

}
