package henu.soft.xiaosi.order.dao;

import henu.soft.xiaosi.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 23:34:48
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    /**
     * 支付成功修改订单状态
     * @param outTradeNo
     * @param code
     */

    void updateOrderStatus(@Param("outTradeNo") String outTradeNo, @Param("code") Integer code);
}
