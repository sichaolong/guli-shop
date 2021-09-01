package henu.soft.xiaosi.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import henu.soft.common.utils.PageUtils;
import henu.soft.xiaosi.product.entity.AttrEntity;
import henu.soft.xiaosi.product.entity.AttrGroupEntity;
import henu.soft.xiaosi.product.vo.AttrGroupRelationVo;
import henu.soft.xiaosi.product.vo.AttrGroupWithAttrsVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 21:04:34
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCatId(Map<String, Object> params, int catId);



    void deleteRelation(AttrGroupRelationVo[] vos);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatlogId(Long catelogId);

}

