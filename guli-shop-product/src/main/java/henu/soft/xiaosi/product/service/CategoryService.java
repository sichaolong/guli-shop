package henu.soft.xiaosi.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import henu.soft.common.utils.PageUtils;
import henu.soft.xiaosi.product.entity.CategoryEntity;
import henu.soft.xiaosi.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 21:04:34
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    Long[] getCatelogPathByCatelogId(Long catelogId);


    void updateAllDetail(CategoryEntity category);


    /**
     * 首页查询一级分类
     * @return
     */
    List<CategoryEntity> getLevel1Categories();

    /**
     * 获取二三级分类，封装成json数据，从数据库查询
     * @return
     */



    Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithLocalLock();

    Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedisLock();

    Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedissonLock();

    /**
     * 获取二三级分类，封装成json数据，整合redis缓存
     * @return
     */
    Map<String, List<Catelog2Vo>> getCatalogJson();

}

