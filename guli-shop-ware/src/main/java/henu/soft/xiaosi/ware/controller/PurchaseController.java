package henu.soft.xiaosi.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import henu.soft.xiaosi.ware.vo.MergeVo;
import henu.soft.xiaosi.ware.vo.PurchaseDoneVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import henu.soft.xiaosi.ware.entity.PurchaseEntity;
import henu.soft.xiaosi.ware.service.PurchaseService;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.utils.R;



/**
 * 采购信息
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 23:36:29
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;


    /**
     * 完成采购
     * @param doneVo
     * @return
     */
    ///ware/purchase/done
    @PostMapping("/done")
    public R finish(@RequestBody PurchaseDoneVo doneVo){

        purchaseService.donePurchases(doneVo);

        return R.ok();
    }

    /**
     * 员工领取已经分配的采购单
     *
     *
     */

    @RequestMapping("/received")
    @RequiresPermissions("ware:purchase:list")
    public R merger(@RequestBody List<Long> purchaseIds){
        //PageUtils page = purchaseService.queryPageUnReceive(params);
        purchaseService.receivePurchases(purchaseIds);

        return R.ok();
    }
    /**
     * 合并采购单
     */

    @RequestMapping("/merge")
    @RequiresPermissions("ware:purchase:list")
    public R merger(@RequestBody MergeVo mergeVo){
        //PageUtils page = purchaseService.queryPageUnReceive(params);
        purchaseService.mergerPurchase(mergeVo);


        return R.ok();
    }


    /**
     * 查找没有人接管的采购单
     */
    @RequestMapping("/unreceive/list")
    @RequiresPermissions("ware:purchase:list")
    public R listUnReceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnReceive(params);


        return R.ok().put("page", page);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);


        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);
		purchase.setUpdateTime(new Date());
		purchase.setCreateTime(new Date());

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
