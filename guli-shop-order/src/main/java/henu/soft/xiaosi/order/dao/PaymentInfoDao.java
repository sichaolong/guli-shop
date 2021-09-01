package henu.soft.xiaosi.order.dao;

import henu.soft.xiaosi.order.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 23:34:47
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
