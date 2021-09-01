package henu.soft.xiaosi.product.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import henu.soft.xiaosi.product.entity.BrandEntity;
import henu.soft.xiaosi.product.vo.BrandVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import henu.soft.xiaosi.product.entity.CategoryBrandRelationEntity;
import henu.soft.xiaosi.product.service.CategoryBrandRelationService;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 21:04:34
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 列表,新增商品三级分类列表
     */
    @RequestMapping("/catelog/list")
    @RequiresPermissions("product:categorybrandrelation:list")
    public R catelogList(@RequestParam("brandId") Long brandId){
        //PageUtils page = categoryBrandRelationService.queryPage(params);

        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.queryByBrandId(brandId);

        return R.ok().put("page", data);
    }

    /**
     * 列表,新增商品 三级分类下的品牌
     */
    @RequestMapping("/brands/list")
    @RequiresPermissions("product:categorybrandrelation:list")
    public R brandsList(@RequestParam("catId") Long catId){
        //PageUtils page = categoryBrandRelationService.queryPage(params);

        List<BrandEntity> brandEntities = categoryBrandRelationService.queryByCatId(catId);

        // 将brandId 和 name 取出来放到vo中

        List<BrandVo> data = brandEntities.stream().map((item) -> {
            BrandVo brandVo = new BrandVo();
            brandVo.setBrandId(item.getBrandId());
            brandVo.setName(item.getName());
            return brandVo;

        }).collect(Collectors.toList());

        return R.ok().put("page", data);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		//categoryBrandRelationService.save(categoryBrandRelation);
        categoryBrandRelationService.saveRelationAndBrandName(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
