package henu.soft.xiaosi.ware.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import java.util.HashMap;



/**
 * 创建MQ的交换机
 */
@Configuration
public class MyMQConfig {


    /**
     * 交换机
     * @return
     */
    @Bean
    public Exchange stockEventExchange(){
       return new TopicExchange("stock-event-exchange",true,false);

    }

    /**
     * 普通队列
     */

    @Bean
    public Queue stockReleaseStockQueue(){

        // String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments
        return new Queue("stock.release.stock.queue",true,false,false);
    }

    /**
     * 延时队列
     */

    @Bean
    public Queue stockDelayStockQueue(){

        // String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments
        /**
         Queue(String name,  队列名字
         boolean durable,  是否持久化
         boolean exclusive,  是否排他
         boolean autoDelete, 是否自动删除
         Map<String, Object> arguments) 属性
         */
        HashMap<String, Object> arguments = new HashMap<>();
        //死信交换机
        arguments.put("x-dead-letter-exchange", "stock-event-exchange");
        //死信路由键
        arguments.put("x-dead-letter-routing-key", "stock.release");
        arguments.put("x-message-ttl", 70000); // 消息过期时间 1分钟
        return new Queue("stock.delay.stock.queue",true,false,false,arguments);
    }

    /**
     * 绑定
     */
    @Bean
    public Binding stockReleaseBinding(){
        //String destination, Binding.DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments
        return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,"stock-event-exchange","stock.release.#",null);
    }

    /**
     * 绑定
     */
    @Bean
    public Binding stockLockedBinding(){
        //String destination, Binding.DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments
        return new Binding("stock.delay.stock.queue", Binding.DestinationType.QUEUE,"stock-event-exchange","stock.delay",null);
    }
}
