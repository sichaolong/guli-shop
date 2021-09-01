package henu.soft.xiaosi.product.vo;

import henu.soft.xiaosi.product.entity.SkuImagesEntity;
import henu.soft.xiaosi.product.entity.SkuInfoEntity;
import henu.soft.xiaosi.product.entity.SpuInfoDescEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class SkuItemVo {

    //1、sku基本信息的获取  pms_sku_info
    private SkuInfoEntity info;

    private boolean hasStock = true;

    //2、sku的图片信息    pms_sku_images
    private List<SkuImagesEntity> images;

    //3、获取spu的销售属性组合
    private List<SkuItemSaleAttrVo> saleAttr;

    //4、获取spu的介绍
    private SpuInfoDescEntity desc;

    //5、获取spu的规格参数信息
    private List<SpuItemAttrGroupVo> groupAttrs;

    // 6. 秒杀信息
    private SeckillInfoVo seckillInfoVo;
}



