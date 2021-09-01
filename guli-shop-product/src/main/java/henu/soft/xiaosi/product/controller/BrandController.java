package henu.soft.xiaosi.product.controller;

import java.util.Arrays;

import java.util.Map;

import henu.soft.common.valid.SaveValidGroup;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import henu.soft.xiaosi.product.entity.BrandEntity;
import henu.soft.xiaosi.product.service.BrandService;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.utils.R;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 21:04:34
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    @RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("product:brand:save")
    public R save( @RequestBody @Validated({SaveValidGroup.class}) BrandEntity brand){

        // 校验异常统一全局处理

        /*

        if(result.hasErrors()){
            List<FieldError> fieldErrors = result.getFieldErrors();
            Map<String,String> map = new HashMap<>();
            fieldErrors.forEach((item)->{
                String field = item.getField();
                String msg = item.getDefaultMessage();
                map.put(field,msg);
            });
            return R.error(400,"信息校验有误！").put("data",map);
        }
        else{
            brandService.save(brand);

            return R.ok();
        }

         */

        brandService.save(brand);

        return R.ok();

    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("product:brand:update")
    public R update(@RequestBody BrandEntity brand){
		//brandService.updateById(brand);
        brandService.updateAllDetail(brand);

        return R.ok();
    }

    /**
     * 修改状态
     */
    @RequestMapping("/update/status")
    @RequiresPermissions("product:brand:update")
    public R updateStatus(@RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
