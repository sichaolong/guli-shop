package henu.soft.xiaosi.order.feign;


import henu.soft.common.to.SkuHasStockTo;
import henu.soft.common.utils.R;
import henu.soft.xiaosi.order.vo.FareVo;
import henu.soft.xiaosi.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("guli-shop-ware")
public interface WareFeignService {

    @RequestMapping("ware/waresku/getSkuHasStocks")
    List<SkuHasStockTo> getSkuHasStocks(@RequestBody List<Long> ids);


    /**
     * 获取运费价格
     * @param addrId
     * @return
     */
    @RequestMapping("ware/wareinfo/fare/{addrId}")
    FareVo getFare(@PathVariable("addrId") Long addrId);


    /**
     * 订单商品锁库存
     * @param itemVos
     * @return
     */
    @RequestMapping("ware/waresku/lock/order")
    R orderLockStock(@RequestBody WareSkuLockVo itemVos);
}
