package henu.soft.xiaosi.order.vo;

import henu.soft.common.to.OrderItemTo;
import lombok.Data;

import java.util.List;

@Data
public class WareSkuLockVo {
    private String OrderSn;

    private List<OrderItemTo> locks;
}
