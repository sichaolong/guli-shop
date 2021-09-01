package henu.soft.xiaosi.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import henu.soft.common.to.mq.SecKillOrderTo;
import henu.soft.common.utils.PageUtils;
import henu.soft.xiaosi.order.entity.OrderEntity;
import henu.soft.xiaosi.order.vo.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 订单
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 23:34:48
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 购物车页去订单确认页
     * @return
     */
    OrderConfirmVo confirmOrder();


    /**
     * 提交订单
     * @param submitVo
     * @return
     */

    SubmitOrderResponseVo submitOrder(OrderSubmitVo submitVo);

    /**
     * 库存回滚，查询订单信息
     * @param orderSn
     * @return
     */

    OrderEntity getOrderByOrderSn(String orderSn);


    /**
     * 收到过期的订单信息，准备关闭订单
     * @param orderEntity
     */
    void closeOrder(OrderEntity orderEntity) throws InvocationTargetException, IllegalAccessException;


    /**
     * 订单支付，查询待支付订单信息
     * @param orderSn
     * @return
     */

    PayVo getOrderPay(String orderSn);

    /**
     * 支付完成跳转订单列表页
     * @param params
     * @return
     */

    PageUtils queryPageWithItem(Map<String, Object> params);


    /**
     * 支付成功回调，修改订单状态
     * @param payAsyncVo
     */
    void handlerPayResult(PayAsyncVo payAsyncVo);

    /**
     * 保存秒杀订单
     * @param orderTo
     */

    void createSeckillOrder(SecKillOrderTo orderTo);



//    String handlePayResult(PayAsyncVo payAsyncVo);
}

