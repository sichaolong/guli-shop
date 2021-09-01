package henu.soft.xiaosi.thirdparty;

import com.aliyun.oss.OSS;
import henu.soft.xiaosi.thirdparty.component.tencent.SendSmsCode;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
@RunWith(SpringRunner.class)
class GuliShopThirdPartyApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    OSS ossClient;




    @Test
    void testUpload() throws FileNotFoundException {
        ossClient.putObject("guli-shop-xiaosi", "11.jpg", new FileInputStream("C:\\Users\\司超龙\\Pictures\\meizi.jpg"));

    }

    @Autowired
    SendSmsCode sendSmsCode;

    @Test
    void testSendSmsCode(){
        System.out.println(sendSmsCode.sendSmsCode("17637821720","123654"));

    }


}
