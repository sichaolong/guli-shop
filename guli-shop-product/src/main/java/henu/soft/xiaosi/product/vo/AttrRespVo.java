package henu.soft.xiaosi.product.vo;

import lombok.Data;

/**
 * 做返回信息，返回关联表的信息
 */

@Data
public class AttrRespVo extends AttrVo{



    private String catelogName;
    private String groupName;
    private Long[] catelogPath;
}
