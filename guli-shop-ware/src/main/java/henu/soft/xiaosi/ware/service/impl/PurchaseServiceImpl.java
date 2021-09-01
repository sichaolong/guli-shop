package henu.soft.xiaosi.ware.service.impl;

import henu.soft.common.constant.WareConstant;
import henu.soft.xiaosi.ware.entity.PurchaseDetailEntity;
import henu.soft.xiaosi.ware.service.PurchaseDetailService;
import henu.soft.xiaosi.ware.service.WareSkuService;
import henu.soft.xiaosi.ware.vo.MergeVo;
import henu.soft.xiaosi.ware.vo.PurchaseDoneVo;
import henu.soft.xiaosi.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.utils.Query;

import henu.soft.xiaosi.ware.dao.PurchaseDao;
import henu.soft.xiaosi.ware.entity.PurchaseEntity;
import henu.soft.xiaosi.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {


    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Autowired
    WareSkuService wareSkuService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {


        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查找未被接受的采购单
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageUnReceive(Map<String, Object> params) {


        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)
        );

        return new PageUtils(page);
    }


    /**
     * 合并采购单
     * - 有分配人
     * - 无分配人
     * @param mergeVo
     */
    @Override
    public void mergerPurchase(MergeVo mergeVo) {

        Long purchaseId = mergeVo.getPurchaseId();
        // 无分配人
        if(purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();

            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatEnum.CREATED.getCode());
            this.save(purchaseEntity);


            // 新增之后，回显填充到实体类
            purchaseId = purchaseEntity.getId();
        }
        // TODO 已经领取的采购单不能在分配


        List<Long> items = mergeVo.getItems();

        // 哪些采购单,封装成多条记录保存
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(i -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(i);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatEnum.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());


        purchaseDetailService.updateBatchById(collect);

        // 更新采购单信息
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);







    }


    /**
     * 员工领取已经被分配的采购单
     * @param purchaseIds
     */
    @Override
    public void receivePurchases(List<Long> purchaseIds) {


        // 更新所有的采购单信息状态
        List<PurchaseEntity> purchaseEntities = purchaseIds.stream().map(i -> {
            PurchaseEntity purchaseEntity = this.getById(i);
            return purchaseEntity;
        }).filter((item) -> {
            // 过滤采购单 确认采购单的状态为 新建、已分配状态
            Integer status = item.getStatus();
            if ( status == WareConstant.PurchaseStatEnum.CREATED.getCode() || status == WareConstant.PurchaseStatEnum.ASSIGNED.getCode()) {
                return true;
            }
            return false;

        }).map((item) -> {
            // 更新状态
            item.setStatus(WareConstant.PurchaseStatEnum.REVEIVE.getCode());

            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        this.updateBatchById(purchaseEntities);



        // 更新采购需求状态
        purchaseEntities.forEach((item)->{

            List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.getPurchaseDetailByPurchaseId(item.getId());

            // 只更新两个属性
            List<PurchaseDetailEntity> collect = purchaseDetailEntities.stream().map(entity -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(entity.getId());
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatEnum.REVEIVE.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());

            purchaseDetailService.updateBatchById(collect);


        });

    }


    /**
     * 员工完成采购单
     * @param doneVo
     */
    @Transactional
    @Override
    public void donePurchases(PurchaseDoneVo doneVo) {
        Long id = doneVo.getId();


        //2、改变采购项的状态
        Boolean flag = true;
        List<PurchaseItemDoneVo> items = doneVo.getItems();

        List<PurchaseDetailEntity> updates = new ArrayList<>();
        //
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            // 某个采购需求有异常
            if(item.getStatus() == WareConstant.PurchaseDetailStatEnum.HASERROR.getCode()){
                flag = false;
                detailEntity.setStatus(item.getStatus());
            }else{
                // 全部采购成功
                detailEntity.setStatus(WareConstant.PurchaseDetailStatEnum.FINISH.getCode());
                ////3、将成功采购的进行入库，更新ware_sku_info
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());

            }
            detailEntity.setId(item.getItemId());
            updates.add(detailEntity);
        }

        purchaseDetailService.updateBatchById(updates);

        //1、改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag?WareConstant.PurchaseStatEnum.FINISH.getCode():WareConstant.PurchaseStatEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

}
