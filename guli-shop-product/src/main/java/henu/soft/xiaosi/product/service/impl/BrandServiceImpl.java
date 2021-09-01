package henu.soft.xiaosi.product.service.impl;

import ch.qos.logback.classic.spi.EventArgUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import henu.soft.xiaosi.product.dao.CategoryBrandRelationDao;
import henu.soft.xiaosi.product.entity.CategoryBrandRelationEntity;
import henu.soft.xiaosi.product.service.CategoryBrandRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.utils.Query;

import henu.soft.xiaosi.product.dao.BrandDao;
import henu.soft.xiaosi.product.entity.BrandEntity;
import henu.soft.xiaosi.product.service.BrandService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {


    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        // 自定义模糊查询
        String key = (String) params.get("key");

        QueryWrapper<BrandEntity> wrapper =  new QueryWrapper<BrandEntity>();
        if(!StringUtils.isEmpty(key)){
            wrapper.like("name",key).or().eq("brand_id",key);
        }

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 更新品牌表 品牌名字段时，为了保证多表数据一致，需要进行 冗余数据表更新
     * @param brand
     */
    @Override
    public void updateAllDetail(BrandEntity brand) {


        this.updateById(brand);

        // 更新其他表
        // TODO
        Long brandId = brand.getBrandId();
        String brandName = brand.getName();

        categoryBrandRelationService.updateAllBrandDetail(brandId,brandName);



    }

}
