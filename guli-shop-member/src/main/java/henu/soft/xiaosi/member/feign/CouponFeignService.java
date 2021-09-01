package henu.soft.xiaosi.member.feign;


import henu.soft.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;


//告诉spring cloud 这个接口是一个远程客户端 调用远程服务
@FeignClient("guli-shop-coupon")//这个远程服务
public interface CouponFeignService {

    @RequestMapping("/coupon/coupon/member/list")
    public R memberCoupons();
}
