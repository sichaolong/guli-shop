package henu.soft.xiaosi.product.service.impl;

import henu.soft.xiaosi.product.dao.AttrAttrgroupRelationDao;
import henu.soft.xiaosi.product.entity.AttrAttrgroupRelationEntity;
import henu.soft.xiaosi.product.entity.AttrEntity;
import henu.soft.xiaosi.product.service.AttrService;
import henu.soft.xiaosi.product.vo.AttrGroupRelationVo;
import henu.soft.xiaosi.product.vo.AttrGroupWithAttrsVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.utils.Query;

import henu.soft.xiaosi.product.dao.AttrGroupDao;
import henu.soft.xiaosi.product.entity.AttrGroupEntity;
import henu.soft.xiaosi.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {


    @Autowired
    AttrAttrgroupRelationDao relationDao;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCatId(Map<String, Object> params, int catId) {

        // 搜索框内容
        String key = (String) params.get("key");
        // 点击三级节点的id
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        // catId 和 key 同时存在
        if(!StringUtils.isEmpty(key)){
            wrapper.and((obj)->{
                obj.eq("catelog_id",key).or().like("attr_group_name",key);
            });
        }
        // 没有指定 catId 即分组id,查全部,或者按照key查
        if (catId == 0){
            return this.queryPage(params);
        }
        else{

            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                   wrapper.eq("catelog_id", catId)
            );

            return new PageUtils(page);

        }
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        //relationDao.delete(new QueryWrapper<>().eq("attr_id",1L).eq("attr_group_id",1L));


        // 封装为关联表的一个实体集合，自定义批量删除方法
        List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map((item) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        // 使用mapper文件完成删除操作
        relationDao.deleteBatchRelation(entities);
    }


    /**
     * 新增商品时，需要获取该分类下的所有属性组、属性
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatlogId(Long catelogId) {

        // 查出当前分类下的所有属性分组ids，封装到vo
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        List<AttrGroupWithAttrsVo> attrGroupWithAttrsVos = attrGroupEntities.stream().map((item) -> {
            AttrGroupWithAttrsVo vo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(item,vo);

            // 查出属性分组的所有属性
            List<AttrEntity> attrs = attrService.getAttrRealtionByGroupId(vo.getAttrGroupId());
            vo.setAttrs(attrs);


            return vo;
        }).collect(Collectors.toList());




        return attrGroupWithAttrsVos;
    }


}
