package henu.soft.xiaosi.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import henu.soft.common.utils.PageUtils;
import henu.soft.xiaosi.product.entity.SpuCommentEntity;

import java.util.Map;

/**
 * 商品评价
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 21:04:33
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

