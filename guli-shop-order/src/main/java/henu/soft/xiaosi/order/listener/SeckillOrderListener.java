package henu.soft.xiaosi.order.listener;

import com.rabbitmq.client.Channel;

import henu.soft.common.to.mq.SecKillOrderTo;
import henu.soft.xiaosi.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = "order.seckill.order.queue")
public class SeckillOrderListener {
    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void createOrder(SecKillOrderTo orderTo, Message message, Channel channel) throws IOException {
        System.out.println("***********接收到秒杀消息");
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            orderService.createSeckillOrder(orderTo);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicReject(deliveryTag,true);
        }
    }
}
