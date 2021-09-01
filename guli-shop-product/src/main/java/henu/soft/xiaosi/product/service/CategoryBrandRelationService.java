package henu.soft.xiaosi.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import henu.soft.common.utils.PageUtils;
import henu.soft.xiaosi.product.entity.BrandEntity;
import henu.soft.xiaosi.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 21:04:34
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);


    List<CategoryBrandRelationEntity> queryByBrandId(Long brandId);

    void saveRelationAndBrandName(CategoryBrandRelationEntity categoryBrandRelation);


    void updateAllBrandDetail(Long brandId, String brandName);

    void updateAllCategoryDetail(Long catId, String categoryName);


    List<BrandEntity> queryByCatId(Long catId);
}

