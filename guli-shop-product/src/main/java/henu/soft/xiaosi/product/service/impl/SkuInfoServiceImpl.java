package henu.soft.xiaosi.product.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import henu.soft.common.to.es.SkuEsModel;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.utils.Query;
import henu.soft.common.utils.R;
import henu.soft.xiaosi.product.dao.SkuInfoDao;
import henu.soft.xiaosi.product.entity.*;
import henu.soft.xiaosi.product.feign.SeckillFeignService;
import henu.soft.xiaosi.product.service.*;
import henu.soft.xiaosi.product.vo.SeckillInfoVo;
import henu.soft.xiaosi.product.vo.SkuItemSaleAttrVo;
import henu.soft.xiaosi.product.vo.SkuItemVo;

import henu.soft.xiaosi.product.vo.SpuItemAttrGroupVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;




@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {



    @Autowired
    SkuImagesService skuImagesService;


    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    SeckillFeignService seckillFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }


    /**
     * 动态条件查询
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        /**
         * key:
         * catelogId: 0
         * brandId: 0
         * min: 0
         * max: 0
         */
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("sku_id", key).or().like("sku_name", key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {

            queryWrapper.eq("catalog_id", catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("brand_id", brandId);
        }

        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            queryWrapper.ge("price", min);
        }

        String max = (String) params.get("max");

        if (!StringUtils.isEmpty(max)) {
            try {
                BigDecimal bigDecimal = new BigDecimal(max);

                if (bigDecimal.compareTo(new BigDecimal("0")) == 1) {
                    queryWrapper.le("price", max);
                }
            } catch (Exception e) {

            }

        }


        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }


    /**
     * 商品上架，根据spuId查询所有的sku信息
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {

        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return list;

    }


    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //1、sku基本信息的获取  pms_sku_info
            SkuInfoEntity skuInfoEntity = this.getById(skuId);
            skuItemVo.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }, executor);

        //2、sku的图片信息    pms_sku_images
        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> skuImagesEntities = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
            skuItemVo.setImages(skuImagesEntities);
        }, executor);


        //3、获取spu的销售属性组合-> 依赖1 获取spuId
        CompletableFuture<Void> saleFuture = infoFuture.thenAcceptAsync((info) -> {
            List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.listSaleAttrs(info.getSpuId());
            System.out.println("获取销售属性====》"+saleAttrVos+"");
            skuItemVo.setSaleAttr(saleAttrVos);
        }, executor);


        //4、获取spu的介绍-> 依赖1 获取spuId
        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((info) -> {
            SpuInfoDescEntity byId = spuInfoDescService.getById(info.getSpuId());
            skuItemVo.setDesc(byId);
        }, executor);


        //5、获取spu的规格参数信息-> 依赖1 获取spuId catalogId
        CompletableFuture<Void> attrFuture = infoFuture.thenAcceptAsync((info) -> {
            List<SpuItemAttrGroupVo> spuItemAttrGroupVos=productAttrValueService.getProductGroupAttrsBySpuId(info.getSpuId(), info.getCatalogId());
            skuItemVo.setGroupAttrs(spuItemAttrGroupVos);
        }, executor);

        // 6.查询当前sku是否参与秒杀优惠
        CompletableFuture<Void> secKillFuture = CompletableFuture.runAsync(() -> {
            R skuSeckillInfo = seckillFeignService.getSkuSeckillInfo(skuId);
            if (skuSeckillInfo.getCode() == 0) {
                SeckillInfoVo seckillInfoVo = skuSeckillInfo.getData(new TypeReference<SeckillInfoVo>() {});
                skuItemVo.setSeckillInfoVo(seckillInfoVo);
            }
        }, executor);

        //等待所有任务执行完成
        try {
            CompletableFuture.allOf(infoFuture, imageFuture, saleFuture, descFuture, attrFuture,secKillFuture).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return skuItemVo;
    }


    /**
     * 商品详情页
     *
     * @param skuId
     * @return
     */
/*
    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();
        //1、sku基本信息的获取  pms_sku_info
        SkuInfoEntity skuInfoEntity = this.getById(skuId);
        skuItemVo.setInfo(skuInfoEntity);
        Long spuId = skuInfoEntity.getSpuId();
        Long catalogId = skuInfoEntity.getCatalogId();


        //2、sku的图片信息    pms_sku_images
        List<SkuImagesEntity> skuImagesEntities = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
        skuItemVo.setImages(skuImagesEntities);

        //3、获取spu的销售属性组合-> 依赖1 获取spuId
        List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.listSaleAttrs(spuId);
        skuItemVo.setSaleAttr(saleAttrVos);

        //4、获取spu的介绍-> 依赖1 获取spuId
        SpuInfoDescEntity byId = spuInfoDescService.getById(spuId);
        skuItemVo.setDesc(byId);

        //5、获取spu的规格参数信息-> 依赖1 获取spuId catalogId
        List<SpuItemAttrGroupVo> spuItemAttrGroupVos = productAttrValueService.getProductGroupAttrsBySpuId(spuId, catalogId);
        skuItemVo.setGroupAttrs(spuItemAttrGroupVos);
        //TODO 6、秒杀商品的优惠信息

        return skuItemVo;
    }
    */


}
