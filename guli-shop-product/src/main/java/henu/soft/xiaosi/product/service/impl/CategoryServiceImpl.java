package henu.soft.xiaosi.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import henu.soft.xiaosi.product.service.CategoryBrandRelationService;
import henu.soft.xiaosi.product.vo.Catelog2Vo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.utils.Query;

import henu.soft.xiaosi.product.dao.CategoryDao;
import henu.soft.xiaosi.product.entity.CategoryEntity;
import henu.soft.xiaosi.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * Redisson分布式锁
     */
    @Autowired
    RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询所有三级分类，封装成树形返回
     *
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        // 1.查出所有的分类信息
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        // 2. Stream流处理各级封装成树形

        // 2.1 一级目录
        List<CategoryEntity> level1 = categoryEntities
                .stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map((categoryEntity) -> {
                    categoryEntity.setChildren(getChildrenCategories(categoryEntity, categoryEntities));
                    return categoryEntity;
                })
                .sorted(Comparator.comparingInt(categoryEntity -> (categoryEntity.getSort() == null ? 0 : categoryEntity.getSort())))
                .collect(Collectors.toList());
        return level1;
    }


    /**
     * 递归获取当前分类的子分类
     *
     * @param current
     * @return
     */
    private List<CategoryEntity> getChildrenCategories(CategoryEntity current, List<CategoryEntity> all) {
        List<CategoryEntity> items = all.stream().filter((item) -> {
            // 第一次调用方法
            // 获取当前分类的子分类
            return item.getParentCid() == current.getCatId();
        }).map((item) -> {
            // 第二次调用方法
            // 当前子分类再找出 子分类
            item.setChildren((getChildrenCategories(item, all)));
            return item;
        }).sorted(Comparator.comparingInt(item -> (item.getSort() == null ? 0 : item.getSort()))).collect(Collectors.toList());
        return items;


    }

    /**
     * 自定义逻辑删除方法
     *
     * @param asList
     */
    @Caching(evict = {
            @CacheEvict(value = "category",key = "'getLevel1Categorys'"),
            @CacheEvict(value = "category",key = "'getCatalogJson'")
    })
    @Override
    public void removeMenuByIds(List<Long> asList) {

        // TODO 1.检查当前删除的菜单，是否被背的地方引用


        // 逻辑删除
        baseMapper.deleteBatchIds(asList);

    }


    /**
     * 为了数据回显，根据第三级 catelogId查 catelogPath
     *
     * @param catelogId
     * @return
     */

    @Override
    public Long[] getCatelogPathByCatelogId(Long catelogId) {

        List<Long> path = new ArrayList<>();

        // 递归找
        helperFindFatherPath(catelogId, path);

        Collections.reverse(path);

        return path.toArray(new Long[path.size()]);
    }


    /**
     * 递归寻找catelogPath
     *
     * @param catelogId
     * @param path
     */

    private void helperFindFatherPath(Long catelogId, List<Long> path) {
        path.add(catelogId);

        Long parentCid = getById(catelogId).getParentCid();
        if (parentCid != 0) {
            helperFindFatherPath(parentCid, path);
        }

    }

    /**
     * 级联更新所有表该字段
     *  @CacheEvict : 失效模式，每次修改就删除缓存
     * @param category
     */
    @Caching(evict = {
            @CacheEvict(value = "category",key = "'getLevel1Categorys'"),
            @CacheEvict(value = "category",key = "'getCatalogJson'")
    })
    @Override
    public void updateAllDetail(CategoryEntity category) {

        this.updateById(category);
        // 更新其他表
        String categoryName = category.getName();
        Long catId = category.getCatId();

        if (!StringUtils.isEmpty(categoryName)) {
            categoryBrandRelationService.updateAllCategoryDetail(catId, categoryName);
        }


    }

    /**
     * 商城首页查询一级分类
     *
     * @return
     */

    @Cacheable(value = {"category"},key = "#root.methodName",sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categories() {

        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));


        return categoryEntities;
    }


    /**
     * 查询首页所有分类数据
     * @return
     */
    // TODO 会产生堆外内存异常解决,目前使用jedis客户端防止luccue客户端的bug


    @Cacheable(value = {"category"},key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

        /**
         * 防止緩存穿透、加上空結果緩存
         * 防止缓存雪崩：设置过期时间
         * 防止缓存击穿：加锁
         */
        System.out.println("使用了SpringCache之后,查询了一次数据库...");
        // 查询全部的分类
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //1 查出所有1级分类
        List<CategoryEntity> level1Catrgorys = getParent_cid(selectList, 0L);

        //2 封装分类
        Map<String, List<Catelog2Vo>> parent_cid = level1Catrgorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1 查询二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            //2 封装上面的结果

            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {

                // map封装
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //1 找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catalog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            //2 封装成指定格式
                            Catelog2Vo.Catalog3Vo catalog3Vo = new Catelog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catalog3Vo;
                        }).collect(Collectors.toList());
                        // 填充三级分类
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));


        return parent_cid;


    }



    /**
     * TODO 1. 本地锁
     * 首页二、三级菜单封装,从数据库查询, 加本地锁锁防止缓存击穿
     *
     * @return
     */
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithLocalLock() {

        /**
         * 防止缓存击穿，需要加锁
         * 1. 先得到锁的，查数据库，更新缓存
         * 2. 后得到锁的，得到锁后需要再次判断缓存中是否有数据，防止重复查数据库
         */

        synchronized (this) {

            // 1. 后拿到锁的再次判断缓存

            return getCatelogFromDb();
        }


    }

    /**
     * TODO 2. Redis锁,原生set xxx nx 方法实现分布式锁
     * 首页二、三级菜单封装,从数据库查询, 加Redis锁防止缓存击穿
     *
     * @return
     */
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedisLock() {

        /**
         * 防止缓存击穿，需要加锁，占redis分布式锁
         * 1. 先得到redis锁的，查数据库，更新缓存
         * 2. 后得到锁的，得到锁后需要再次判断缓存中是否有数据，防止重复查数据库
         *
         *
         */

        // TODO 2. 加锁和设置过期时间必须是原子的

        // TODO 3. 需要保证删除自己的锁

        String uuid = UUID.randomUUID().toString();

        Boolean lockResult = stringRedisTemplate.opsForValue().setIfAbsent("mylock", uuid, 300, TimeUnit.SECONDS);

        // redis加锁成功
        if (lockResult == true) {

            // TODO 1.若是抛异常或者故障 没有删除锁，会导致死锁问题，因此需要设置过期时间
            // 设置过期时间
            //stringRedisTemplate.expire("mylock",30,TimeUnit.SECONDS);
            // 查数据库，设置缓存


            Map<String, List<Catelog2Vo>> catelogFromDb;
            try {

                catelogFromDb = getCatelogFromDb();
            } finally {

                // 删除锁前判断是不是自己的锁
                // TODO 4. 判断和删锁需要是原子操作，使用lua删锁脚本

                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                Long res = stringRedisTemplate.execute(new
                                DefaultRedisScript<Long>(script, Long.class)
                        , Arrays.asList("mylock"), uuid);

            /*
            if(stringRedisTemplate.opsForValue().get("mylock").equals(uuid)){
                stringRedisTemplate.delete("myock");
            }

             */

            }


            return catelogFromDb;
        } else {
            // 加锁失败。。。自旋重试
            try {
                Thread.sleep(200);
            } catch (Exception e) {
            }

            return getCatalogJsonFromDBWithRedisLock();
        }


    }

    /**
     * TODO 3. Redisson分布式锁，基于原生set xxx nx 封装的 分布式锁、对象的框架,主要是为了应对分布式集群架构
     * 首页二、三级菜单封装,从数据库查询, 加Redis分布式锁防止缓存击穿
     *
     * @return
     */
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedissonLock() {

        /**
         * 防止缓存击穿，需要加锁，占redis分布式锁
         * 1. 先得到redis锁的，查数据库，更新缓存
         * 2. 后得到锁的，得到锁后需要再次判断缓存中是否有数据，防止重复查数据库
         *
         *
         */

        // 注意锁的名字，锁的粒度
        RLock lock = redissonClient.getLock("my-catalogJson-lock");
        lock.lock();


        Map<String, List<Catelog2Vo>> catelogFromDb;
        try {
            catelogFromDb = getCatelogFromDb();
        } finally {
            lock.unlock();
        }


        return catelogFromDb;


    }


    /**
     * 原生的方式使用缓存，现在直接使用SpringCache注解代替
     * @return
     */

    public Map<String, List<Catelog2Vo>> getCatalogJsonDemo() {

        /**
         * 防止緩存穿透、加上空結果緩存
         * 防止缓存雪崩：设置过期时间
         * 防止缓存击穿：加锁
         */
        // 1. 查询缓存

        String catalogJSON = stringRedisTemplate.opsForValue().get("catalogJSON");
        // 缓存中没有
        if (StringUtils.isEmpty(catalogJSON)) {
            // 2. 查询数据库，获取三级分类全部信息,并将信息放入缓存
            Map<String, List<Catelog2Vo>> catalogJsonFromDB = getCatalogJsonFromDBWithRedissonLock();


            return catalogJsonFromDB;
        }

        // 缓存中有
        // 4.反序列化，json转为对象
        TypeReference<Map<String, List<Catelog2Vo>>> mapTypeReference = new TypeReference<>() {
        };

        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, mapTypeReference);
        return result;


    }


    /**
     * 查询数据库获取分类方法
     *
     * @return
     */

    private Map<String, List<Catelog2Vo>> getCatelogFromDb() {


        String catalogJSON = stringRedisTemplate.opsForValue().get("catalogJSON");
        // 缓存有
        if (!StringUtils.isEmpty(catalogJSON)) {
            TypeReference<Map<String, List<Catelog2Vo>>> mapTypeReference = new TypeReference<>() {
            };

            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, mapTypeReference);

            return result;
        }


        System.out.println("查询了一次数据库...");
        // 查询全部的分类
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //1 查出所有1级分类
        List<CategoryEntity> level1Catrgorys = getParent_cid(selectList, 0L);

        //2 封装分类
        Map<String, List<Catelog2Vo>> parent_cid = level1Catrgorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1 查询二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            //2 封装上面的结果

            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {

                // map封装
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //1 找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catalog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            //2 封装成指定格式
                            Catelog2Vo.Catalog3Vo catalog3Vo = new Catelog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catalog3Vo;
                        }).collect(Collectors.toList());
                        // 填充三级分类
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        //3. 释放锁之前将数据放入redis

        // 查到的数据放入缓存,转化为json,取出来的时候，还需要反序列化

        String s = JSON.toJSONString(parent_cid);
        // 设置过期时间
        stringRedisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);

        return parent_cid;
    }

    /**
     * sql查询优化，只查一次数据库获取三级分类，再利用该方法获取封装数据
     *
     * @param selectList
     * @param parent_cid
     * @return
     */

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> {
            return item.getParentCid() == parent_cid;
        }).collect(Collectors.toList());
//        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
        return collect;
    }



}
