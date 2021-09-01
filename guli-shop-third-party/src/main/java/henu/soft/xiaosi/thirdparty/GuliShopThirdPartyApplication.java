package henu.soft.xiaosi.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GuliShopThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliShopThirdPartyApplication.class, args);
    }

}
