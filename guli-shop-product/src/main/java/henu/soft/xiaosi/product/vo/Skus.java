/**
  * Copyright 2021 bejson.com
  */
package henu.soft.xiaosi.product.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2021-07-29 17:38:24
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Skus {

    // 1. sku销售属性sku_sale_attr_value
    private List<Attr> attr;

    // 2. sku基本信息sku_info
    private String skuName;
    private BigDecimal price;
    private String skuTitle;
    private String skuSubtitle;

    // 3. sku图片集 sku_images
    private List<Images> images;
    private List<String> descar;

    // 4. sku 的优惠满减信息，其他数据库的表
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;


}
