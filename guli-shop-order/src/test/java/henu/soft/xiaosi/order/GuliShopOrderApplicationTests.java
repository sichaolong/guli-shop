package henu.soft.xiaosi.order;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import henu.soft.common.to.SkuInfoTo;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest

class GuliShopOrderApplicationTests {

    @Test
    void contextLoads() {
    }


    //@RabbitListener(queues = {"xxxQueue"})
    public void receiveMessage(Message message, SkuInfoTo skuInfoTo, Channel channel){
        // 1. 得到字节数组，需要自己转化
        byte[] body = message.getBody();
        MessageProperties messageProperties = message.getMessageProperties();


        // 2. 直接得到对应实体对象
        System.out.println(skuInfoTo);

        // 3. 得到当前传输数据的通道
        System.out.println(channel);
        // 通道内自增，按照消息传送的数量自增
        long tag = messageProperties.getDeliveryTag();

        // 手动ack
        try {
            /**
             * 消费确认
             * 后面参数为是否批量接受
             */
            channel.basicAck(tag,false);

            /**
             * 消费拒绝
             * 后面参数为是否批量接受，拒绝消费的消息如何处理（true：发回服务器，false:丢弃)
             */
            channel.basicNack(tag,false,false);


            /**
             * 消费拒绝
             * 后面参数为是否批量接受
             */
            channel.basicReject(tag,false);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
