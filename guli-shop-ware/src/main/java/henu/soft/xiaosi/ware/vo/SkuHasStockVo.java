package henu.soft.xiaosi.ware.vo;

import lombok.Data;

/**
 * 商品上架查询是否有库存
 */

@Data
public class SkuHasStockVo {

    private Long skuId;
    private Boolean hasStock;
}
