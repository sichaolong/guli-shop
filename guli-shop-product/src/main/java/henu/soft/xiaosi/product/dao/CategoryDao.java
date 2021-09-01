package henu.soft.xiaosi.product.dao;

import henu.soft.xiaosi.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 21:04:34
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
