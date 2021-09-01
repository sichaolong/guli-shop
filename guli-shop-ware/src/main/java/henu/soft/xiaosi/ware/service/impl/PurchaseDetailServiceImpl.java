package henu.soft.xiaosi.ware.service.impl;


import com.alibaba.cloud.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.utils.Query;

import henu.soft.xiaosi.ware.dao.PurchaseDetailDao;
import henu.soft.xiaosi.ware.entity.PurchaseDetailEntity;
import henu.soft.xiaosi.ware.service.PurchaseDetailService;



@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String wareId = (String) params.get("wareId");


        /**
         * sku_id、purcase_id 、key模糊查询
         */
        if(!StringUtils.isEmpty(key)){
            wrapper.and((obj)->{
                obj.eq("sku_id",key).or().eq("purchase_id",key);
            });
        }

        if(!StringUtils.isEmpty(status)){
            wrapper.eq("status",status);
        }
        if(!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
               wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 根据采购单查询所有的 采购需求单，方便修改状态
     * @param id
     * @return
     */
    @Override
    public List<PurchaseDetailEntity> getPurchaseDetailByPurchaseId(Long id) {

        return this.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", id));
    }

}
