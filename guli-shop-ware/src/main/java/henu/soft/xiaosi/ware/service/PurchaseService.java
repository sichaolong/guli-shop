package henu.soft.xiaosi.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import henu.soft.common.utils.PageUtils;
import henu.soft.xiaosi.ware.entity.PurchaseEntity;
import henu.soft.xiaosi.ware.vo.MergeVo;
import henu.soft.xiaosi.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 23:36:29
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnReceive(Map<String, Object> params);


    void mergerPurchase(MergeVo mergeVo);


    void receivePurchases(List<Long> purchaseIds);

    void donePurchases(PurchaseDoneVo doneVo);

}

