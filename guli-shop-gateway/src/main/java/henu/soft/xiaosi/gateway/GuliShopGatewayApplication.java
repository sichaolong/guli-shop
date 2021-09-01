package henu.soft.xiaosi.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class GuliShopGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliShopGatewayApplication.class, args);
    }

}
