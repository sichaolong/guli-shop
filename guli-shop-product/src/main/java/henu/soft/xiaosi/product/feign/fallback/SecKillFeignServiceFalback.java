package henu.soft.xiaosi.product.feign.fallback;


import henu.soft.common.exception.BizCodeEnume;
import henu.soft.common.utils.R;
import henu.soft.xiaosi.product.feign.SeckillFeignService;
import org.springframework.stereotype.Component;

/**
 * <p>Title: SecKillFeignServiceFalback</p>
 * Description：
 * date：2020/7/10 16:03
 */
@Component
public class SecKillFeignServiceFalback implements SeckillFeignService {

	@Override
	public R getSkuSeckillInfo(Long skuId) {
		System.out.println("触发熔断");
		//return R.error();
		return R.error(BizCodeEnume.TO_MANY_REQUEST.getCode(), BizCodeEnume.TO_MANY_REQUEST.getMsg());
	}
}
