package henu.soft.xiaosi.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import henu.soft.common.utils.PageUtils;
import henu.soft.xiaosi.product.entity.AttrAttrgroupRelationEntity;
import henu.soft.xiaosi.product.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 21:04:34
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addBetchAttrGroupRelations(List<AttrGroupRelationVo> vos);
}

