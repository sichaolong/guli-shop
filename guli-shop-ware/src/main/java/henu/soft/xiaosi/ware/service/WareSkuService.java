package henu.soft.xiaosi.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import henu.soft.common.to.SkuHasStockTo;
import henu.soft.common.to.mq.OrderTo;
import henu.soft.common.to.mq.StockLockedTo;
import henu.soft.common.utils.PageUtils;
import henu.soft.xiaosi.ware.entity.WareSkuEntity;
import henu.soft.xiaosi.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 23:36:29
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    /**
     * 商品上架
     * 查询sku是否有库存
     * @return
     */

    List<SkuHasStockTo> getSkuHasStock(List<Long> skuIds);


    /**
     * 提交订单锁定库存
     * @param lockVo
     * @return
     */
    Boolean orderLockStock(WareSkuLockVo lockVo);


    /**
     * 解锁库存
     * @param stockLockedTo
     */
    void unlock(StockLockedTo stockLockedTo);

    void unlock(OrderTo orderTo);

}

