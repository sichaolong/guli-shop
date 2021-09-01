package henu.soft.xiaosi.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import henu.soft.common.utils.PageUtils;
import henu.soft.xiaosi.product.entity.SkuSaleAttrValueEntity;
import henu.soft.xiaosi.product.vo.SkuItemSaleAttrVo;

import java.util.List;
import java.util.Map;
/**
 * sku销售属性&值
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-01 21:08:49
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询点击进入商品详情页的信息
     * @param spuId
     * @return
     */

    List<SkuItemSaleAttrVo> listSaleAttrs(Long spuId);


    /**
     * 商品添加值购物车
     * @param skuId
     * @return
     */
    List<String> getSkuSaleAttrValuesAsString(Long skuId);

}

