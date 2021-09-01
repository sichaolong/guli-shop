package henu.soft.xiaosi.seckill.service;



import henu.soft.xiaosi.seckill.to.SeckillSkuRedisTo;

import java.util.List;


public interface SeckillService {

	void uploadSeckillSkuLatest3Day();

	/**
	 * 获取当前可以参与秒杀的商品信息
	 */
	List<SeckillSkuRedisTo> getCurrentSeckillSkus();

	SeckillSkuRedisTo getSkuSeckillInfo(Long skuId);

	String kill(String killId, String key, Integer num);
}
