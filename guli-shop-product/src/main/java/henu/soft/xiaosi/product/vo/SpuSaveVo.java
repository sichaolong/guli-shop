/**
  * Copyright 2021 bejson.com
  */
package henu.soft.xiaosi.product.vo;
import com.google.common.eventbus.AllowConcurrentEvents;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2021-07-29 17:38:24
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpuSaveVo {

    // spu_info
    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    private BigDecimal weight;
    private int publishStatus;

    // 分表1-spu_info_desc描述信息
    private List<String> decript;

    // 分表2-spu_images图片集
    private List<String> images;
    private Bounds bounds;
    // 分表3-spu_product_attr_value规格参数
    private List<BaseAttrs> baseAttrs;
    // sku信息
    private List<Skus> skus;


}
