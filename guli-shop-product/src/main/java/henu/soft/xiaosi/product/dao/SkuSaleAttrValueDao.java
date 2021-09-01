package henu.soft.xiaosi.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import henu.soft.xiaosi.product.entity.SkuSaleAttrValueEntity;
import henu.soft.xiaosi.product.vo.SkuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-01 21:08:49
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuItemSaleAttrVo> listSaleAttrs(Long spuId);

    /**
     * 商品添加购物车查询属性
     * @param skuId
     * @return
     */

    List<String> getSkuSaleAttrValuesAsString(Long skuId);

}
