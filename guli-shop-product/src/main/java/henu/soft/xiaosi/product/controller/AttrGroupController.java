package henu.soft.xiaosi.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import henu.soft.xiaosi.product.entity.AttrEntity;
import henu.soft.xiaosi.product.service.AttrAttrgroupRelationService;
import henu.soft.xiaosi.product.service.AttrService;
import henu.soft.xiaosi.product.service.CategoryService;
import henu.soft.xiaosi.product.vo.AttrGroupRelationVo;
import henu.soft.xiaosi.product.vo.AttrGroupWithAttrsVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import henu.soft.xiaosi.product.entity.AttrGroupEntity;
import henu.soft.xiaosi.product.service.AttrGroupService;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.utils.R;



/**
 * 属性分组
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 21:04:34
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    AttrService attrService;


    @Autowired
    AttrAttrgroupRelationService relationService;


    /**
     * 新增商品
     *
     */

    /**
     * 根据 属性分类id 查询所有的属性
     */
    @RequestMapping("/{catelogId}/withattr")
    @RequiresPermissions("product:attrgroup:info")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId){

        // 查出当前分类下的所有属性分组

        // 查出属性分组的所有属性

       List<AttrGroupWithAttrsVo> data =  attrGroupService.getAttrGroupWithAttrsByCatlogId(catelogId);


        return R.ok().put("data", data);
    }


    /***
     * 品牌关联属性
     */
    /**
     * 列表
     */
    @RequestMapping("/list/{catId}")
    @RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,@PathVariable int catId){
        //PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPageByCatId(params,catId);

        return R.ok().put("page", page);
    }

    /**
     * 根据 属性分类id 查询所有的属性
     */
    @RequestMapping("/{attrGroupId}/attr/relation")
    @RequiresPermissions("product:attrgroup:info")
    public R getAttrRelation(@PathVariable("attrGroupId") Long attrGroupId){

       List<AttrEntity> list = attrService.getAttrRealtionByGroupId(attrGroupId);
        return R.ok().put("data", list);
    }

    /**
     * 根据 属性分类id 查出来分组中未关联的
     */
    @RequestMapping("/{attrGroupId}/noattr/relation")
    @RequiresPermissions("product:attrgroup:info")
    public R getAttrNoRelation(@PathVariable("attrGroupId") Long attrGroupId,@RequestParam Map<String,Object> params){

        PageUtils page = attrService.getAttrNoRealtionByGroupId(params,attrGroupId);
        return R.ok().put("page", page);
    }


    @RequestMapping("/attr/relation")
    @RequiresPermissions("product:attrgroup:info")
    public R saveRelations(@RequestBody List<AttrGroupRelationVo> vos){

        relationService.addBetchAttrGroupRelations(vos);
        return R.ok();
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    @RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

		// 为了根据第三级catelogId查出catelogPath
        Long catelogId = attrGroup.getCatelogId();
        Long[] catelogPath = categoryService.getCatelogPathByCatelogId(catelogId);

        // 设置返回前端
        attrGroup.setCatelogPath(catelogPath);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 移除关联
     */
    @RequestMapping("/attr/relation/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody AttrGroupRelationVo[] vos){
        attrGroupService.deleteRelation(vos);

        return R.ok();
    }
    /**
     * 删除分类
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
