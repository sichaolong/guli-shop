package henu.soft.xiaosi.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import henu.soft.common.utils.PageUtils;
import henu.soft.xiaosi.coupon.entity.SeckillPromotionEntity;

import java.util.Map;

/**
 * 秒杀活动
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 23:30:21
 */
public interface SeckillPromotionService extends IService<SeckillPromotionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

