package henu.soft.xiaosi.ware.feign;


import henu.soft.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("guli-shop-order")
public interface OrderFeignService {

    @RequestMapping("order/order/infoByOrderSn/{OrderSn}")
    R infoByOrderSn(@PathVariable("OrderSn") String OrderSn);
}
