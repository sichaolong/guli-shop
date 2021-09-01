package henu.soft.xiaosi.seckill.feign;


import henu.soft.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * <p>Title: CouponFeignService</p>
 * Description：
 * date：2020/7/6 17:35
 */
@FeignClient("guli-shop-coupon")
public interface CouponFeignService {

	@GetMapping("/coupon/seckillsession/lates3DaySession")
	R getLate3DaySession();
}
