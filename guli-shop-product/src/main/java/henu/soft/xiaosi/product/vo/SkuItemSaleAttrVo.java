package henu.soft.xiaosi.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SkuItemSaleAttrVo {

    private Long attrId;

    private String attrName;

    private List<AttrValueWithSkuIdVo> attrValues;
    //private String attrValue 属性值
    //private String skuIds 该属性值对应的skuId的集合

}
