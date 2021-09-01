package henu.soft.xiaosi.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import henu.soft.common.exception.BizCodeEnume;
import henu.soft.common.exception.NoStockException;
import henu.soft.common.to.SkuHasStockTo;
import henu.soft.xiaosi.ware.vo.SkuHasStockVo;
import henu.soft.xiaosi.ware.vo.WareSkuLockVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import henu.soft.xiaosi.ware.entity.WareSkuEntity;
import henu.soft.xiaosi.ware.service.WareSkuService;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.utils.R;



/**
 * 商品库存
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 23:36:29
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;



    /**
     * 下订单时锁库存
     * @param itemVos
     * @return
     */
    @RequestMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo lockVo) {
        try {
            Boolean lock = wareSkuService.orderLockStock(lockVo);
            return R.ok();
        } catch (NoStockException e) {
            return R.error(BizCodeEnume.NO_STOCK_EXCEPTION.getCode(), BizCodeEnume.NO_STOCK_EXCEPTION.getMsg());
        }
    }


    /**
     * 订单确认页查询是否有库存
     * @param ids
     * @return
     */

    @RequestMapping("/getSkuHasStocks")
    List<SkuHasStockTo> getSkuHasStocks(@RequestBody List<Long> ids){
        return wareSkuService.getSkuHasStock(ids);
    }
    /**
     * 商品上架
     * 查询sku是否有库存
     */

    @PostMapping("/hasstock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds){

        List <SkuHasStockTo> list = wareSkuService.getSkuHasStock(skuIds);

        R ok = R.ok();
        ok.setData(list);
        return ok;
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
