package henu.soft.xiaosi.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import henu.soft.xiaosi.product.dao.BrandDao;
import henu.soft.xiaosi.product.dao.CategoryDao;
import henu.soft.xiaosi.product.entity.BrandEntity;
import henu.soft.xiaosi.product.entity.CategoryEntity;
import henu.soft.xiaosi.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.utils.Query;

import henu.soft.xiaosi.product.dao.CategoryBrandRelationDao;
import henu.soft.xiaosi.product.entity.CategoryBrandRelationEntity;
import henu.soft.xiaosi.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    BrandDao brandDao;




    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }


    /**
     * 自定义查询方法
     * 根据 品牌id 查询关联的 category分类
     *
     * @return
     */
    @Override
    public List<CategoryBrandRelationEntity> queryByBrandId(Long brandId) {

        List<CategoryBrandRelationEntity> data = this.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId)
        );
        return data;



    }


    /**
     * 自定义保存方法
     * 不仅要保存品牌和分类的关系，还要 查出来品牌名字、分类名字在保存
     * @param categoryBrandRelation
     */
    @Override
    public void saveRelationAndBrandName(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();

        // 查询品牌名，分类名
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        //CategoryEntity  categoryEntity = categoryDao.selectOne(new QueryWrapper<CategoryEntity>().eq("cat_id", catelogId));
        String categoryName = categoryEntity.getName();

        BrandEntity brandEntity = brandDao.selectById(brandId);
        String brandName = brandEntity.getName();


        categoryBrandRelation.setBrandName(brandName);
        categoryBrandRelation.setCatelogName(categoryName);

        this.save(categoryBrandRelation);



    }


    /**
     * 更新品牌名字段，所有该字段都要更新
     * @param brandId
     * @param brandName
     */
    @Override
    public void updateAllBrandDetail(Long brandId, String brandName) {

        this.update(
                new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId).set("brand_name",brandName)
        );


    }

    /**
     * 更新三级分类字段，所有表该字段都要更新
     * @param catId
     * @param categoryName
     */

    @Override
    public void updateAllCategoryDetail(Long catId, String categoryName) {
        this.update(
                new UpdateWrapper<CategoryBrandRelationEntity>().eq("catelog_id",catId).set("catelog_name",categoryName)
        );
    }

    /**
     * 新增 商品时
     * 根据三级分类id,查品牌列表
     * @param catId
     * @return
     */

    @Override
    public List<BrandEntity> queryByCatId(Long catId) {


        // 查中间表
        List<CategoryBrandRelationEntity> relationEntities = baseMapper.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
        List<Long> brandIds = relationEntities.stream().map((item) -> {
            return item.getBrandId();
        }).collect(Collectors.toList());


        // 根据 brandIds 查品牌表
        List<BrandEntity> brandEntities = brandDao.selectBatchIds(brandIds);


        return brandEntities;
    }


}
