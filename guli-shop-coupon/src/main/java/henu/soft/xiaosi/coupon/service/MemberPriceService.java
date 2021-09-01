package henu.soft.xiaosi.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import henu.soft.common.utils.PageUtils;
import henu.soft.xiaosi.coupon.entity.MemberPriceEntity;

import java.util.Map;

/**
 * 商品会员价格
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 23:30:21
 */
public interface MemberPriceService extends IService<MemberPriceEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

