package henu.soft.xiaosi.order.feign;


import henu.soft.common.to.OrderItemTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@FeignClient("guli-shop-cart")
public interface CartFeignService {


    @RequestMapping("/getCheckedItems")
    List<OrderItemTo> getCheckedItems();
}
