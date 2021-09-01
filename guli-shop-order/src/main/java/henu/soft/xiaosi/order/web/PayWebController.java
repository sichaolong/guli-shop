package henu.soft.xiaosi.order.web;

import com.alipay.api.AlipayApiException;

import henu.soft.xiaosi.order.config.AlipayTemplate;
import henu.soft.xiaosi.order.service.OrderService;
import henu.soft.xiaosi.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PayWebController {

    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private OrderService orderService;

    @ResponseBody
    @GetMapping(value = "/aliPayOrder",produces = "text/html")
    public String aliPayOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
        System.out.println("接收到订单信息orderSn："+orderSn);
        /**
         * 获取当前订单的支付信息，订单号、价格等
         */
        PayVo payVo = orderService.getOrderPay(orderSn);
        String pay = alipayTemplate.pay(payVo);
        return pay;
    }


}
