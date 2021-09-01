package henu.soft.xiaosi.coupon.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import jdk.nashorn.internal.ir.CallNode;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.utils.Query;

import henu.soft.xiaosi.coupon.dao.SeckillSkuRelationDao;
import henu.soft.xiaosi.coupon.entity.SeckillSkuRelationEntity;
import henu.soft.xiaosi.coupon.service.SeckillSkuRelationService;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {


    /**
     * 根据秒杀场次的sessionId
     * 查询秒杀场次的关联商品列表
     * @param params
     * @return
     */


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SeckillSkuRelationEntity> wrapper =  new QueryWrapper<SeckillSkuRelationEntity>();

        // 获取秒杀时间段关联的商品,场次id
        String promotionSessionId = (String) params.get("promotionSessionId");

        if (!StringUtils.isEmpty(promotionSessionId)){
            wrapper.eq("promotion_session_id",promotionSessionId);
        }

        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}
