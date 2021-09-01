package henu.soft.xiaosi.order.dao;

import henu.soft.xiaosi.order.entity.OrderOperateHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单操作历史记录
 * 
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 23:34:48
 */
@Mapper
public interface OrderOperateHistoryDao extends BaseMapper<OrderOperateHistoryEntity> {
	
}
