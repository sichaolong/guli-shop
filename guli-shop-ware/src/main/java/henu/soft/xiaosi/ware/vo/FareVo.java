package henu.soft.xiaosi.ware.vo;

import henu.soft.common.to.MemberAddressTo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareVo {
    private MemberAddressTo address;
    private BigDecimal fare;
}

