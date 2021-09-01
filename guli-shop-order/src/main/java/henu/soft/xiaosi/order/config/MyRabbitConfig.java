package henu.soft.xiaosi.order.config;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MyRabbitConfig {


    /**
     * 配置rabbitmq的对象消息序列化机制为json
     * @return
     */

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }


    @Autowired
    RabbitTemplate rabbitTemplate;



    @PostConstruct
    public void initRabbitTemplate(){

        /**
         * 1. 设置确认回调（生产消息--》服务器)
         */
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             *
             * @param correlationData 当前消息的唯一关联数据（消息的唯一id）
             * @param b 消息是否成功到达rabbitmq服务器
             * @param s 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {

            }
        });

        /**
         * 2. 设置抵达队列回调（生产消息---》队列)
         */
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            /**
             *
             * @param returnedMessage
             */
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {

            }
        });



        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {

            /**
             *  已过时
             * @param message 失败消息详细信息
             * @param i 回复的状态码
             * @param s 回复的文本内容
             * @param s1 目标交换机
             * @param s2 目标路由键
             */
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {

            }
        });


        /**
         * 3. 设置手动ack
         */


    }



}
