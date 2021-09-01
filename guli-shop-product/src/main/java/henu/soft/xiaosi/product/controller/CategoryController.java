package henu.soft.xiaosi.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import henu.soft.xiaosi.product.entity.CategoryEntity;
import henu.soft.xiaosi.product.service.CategoryService;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.utils.R;



/**
 * 商品三级分类
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 21:04:34
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */

    /**
     * 查询所有分类、子分类，使用树形结构封装
     *
     * @return
     */
    @RequestMapping("/list/tree")
    @RequiresPermissions("product:category:list")
    public R list(){
        //PageUtils page = categoryService.queryPage(params);

        List<CategoryEntity> list = categoryService.listWithTree();

        return R.ok().put("page", list);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    @RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		//categoryService.updateById(category);
        // 更新需要级联更新所有表的该字段

        categoryService.updateAllDetail(category);

        return R.ok();
    }

    /**
     * 批量排序修改
     */
    @RequestMapping("/update/sort")
    @RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity[] array){
        categoryService.updateBatchById(Arrays.asList(array));

        return R.ok();
    }

    /**
     * 删除
     */
    /**
     * 自定义逻辑删除方法
     * @param catIds
     * @return
     */
    @RequestMapping("/delete")
    @RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
		//categoryService.removeByIds(Arrays.asList(catIds));
        categoryService.removeMenuByIds(Arrays.asList(catIds));

        return R.ok();
    }

}
