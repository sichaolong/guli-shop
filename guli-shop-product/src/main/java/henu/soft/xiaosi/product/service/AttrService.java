package henu.soft.xiaosi.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import henu.soft.common.utils.PageUtils;
import henu.soft.xiaosi.product.entity.AttrEntity;
import henu.soft.xiaosi.product.vo.AttrGroupRelationVo;
import henu.soft.xiaosi.product.vo.AttrRespVo;
import henu.soft.xiaosi.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-01 21:08:49
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);



    List<AttrEntity> getAttrRealtionByGroupId(Long attrGroupId);

    PageUtils getAttrNoRealtionByGroupId(Map<String, Object> params, Long attrGroupId);


    /**
     * 指定属性集合筛选出能搜索的属性
     * @param attrIds
     * @return
     */
    List<Long> selectSearchAttrIds(List<Long> attrIds);


}

