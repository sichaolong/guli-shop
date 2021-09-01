package henu.soft.xiaosi.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MyRedissonConfig {
    /**
     * 所有的redis操作都是通过RedissionClient对象
     * @return
     * @throws IOException
     */

    @Bean(destroyMethod="shutdown")
    RedissonClient redisson() throws IOException {
        Config config = new Config();
        // 多节点
        //  config.useClusterServers()
        //            .addNodeAddress("127.0.0.1:7004", "127.0.0.1:7001");

        // 单节点
        config.useSingleServer()
                .setAddress("redis://47.108.148.53:6379");
        // 创建实例
        return Redisson.create(config);
    }
}
