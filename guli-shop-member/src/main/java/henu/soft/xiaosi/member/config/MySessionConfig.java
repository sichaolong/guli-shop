package henu.soft.xiaosi.member.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;


/**
 * 分布式session
 * 提升session的作用域，便于父域名能够使用session
 */
@Configuration
public class MySessionConfig {

    /**
     * 配置session的一些信息
     * @return
     */

    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();

        cookieSerializer.setDomainName("gulishop.cn");
        cookieSerializer.setCookieName("GULISHOPSESSION");

        return cookieSerializer;
    }

    /**
     * 序列化方式
     * @return
     */

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
        return new GenericFastJsonRedisSerializer();
    }
}
