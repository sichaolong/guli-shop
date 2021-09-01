package henu.soft.xiaosi.ware.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.TypeReference;
import henu.soft.common.exception.NoStockException;
import henu.soft.common.to.SkuHasStockTo;
import henu.soft.common.to.mq.OrderTo;
import henu.soft.common.to.mq.StockDetailTo;
import henu.soft.common.to.mq.StockLockedTo;
import henu.soft.common.utils.R;
import henu.soft.xiaosi.ware.entity.WareOrderTaskDetailEntity;
import henu.soft.xiaosi.ware.entity.WareOrderTaskEntity;
import henu.soft.xiaosi.ware.enume.OrderStatusEnum;
import henu.soft.xiaosi.ware.enume.WareTaskStatusEnum;
import henu.soft.xiaosi.ware.feign.OrderFeignService;
import henu.soft.xiaosi.ware.feign.ProductFeignService;
import henu.soft.xiaosi.ware.service.WareOrderTaskDetailService;
import henu.soft.xiaosi.ware.service.WareOrderTaskService;
import henu.soft.xiaosi.ware.vo.OrderItemVo;
import henu.soft.xiaosi.ware.vo.SkuHasStockVo;
import henu.soft.xiaosi.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.utils.Query;

import henu.soft.xiaosi.ware.dao.WareSkuDao;
import henu.soft.xiaosi.ware.entity.WareSkuEntity;
import henu.soft.xiaosi.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
@RabbitListener
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {


    @Autowired
    WareSkuDao wareSkuDao;


    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    RabbitTemplate rabbitTemplate;


    @Autowired
    OrderFeignService orderFeignService;

    /**
     * 重新查询条件
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();

        String skuId = (String) params.get("skuId");
        String wareId = (String) params.get("wareId");

        if (!StringUtils.isEmpty(skuId)) {
            wrapper.eq("sku_id", skuId).or().like("sku_id", skuId);
        }
        if (!StringUtils.isEmpty(wareId)) {
            wrapper.eq("ware_id", wareId).or().like("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }


    /**
     * 采购单完成，需要远程调用
     *
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    @Transactional
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {

        //1、判断如果还没有这个库存记录新增
        List<WareSkuEntity> entities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (entities == null || entities.size() == 0) {

            // 增加记录
            WareSkuEntity skuEntity = new WareSkuEntity();
            skuEntity.setSkuId(skuId);
            // 冗余字段
            skuEntity.setStock(skuNum);
            skuEntity.setWareId(wareId);
            skuEntity.setStockLocked(0);
            //TODO 远程查询sku的名字，如果失败，整个事务无需回滚
            //1、自己catch异常
            //TODO 还可以用什么办法让异常出现以后不回滚？高级
            try {
                R info = productFeignService.info(skuId);
                Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");

                if (info.getCode() == 0) {
                    skuEntity.setSkuName((String) data.get("skuName"));
                }
            } catch (Exception e) {

            }


            wareSkuDao.insert(skuEntity);
        } else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }

    }

    /**
     * 商品上架
     * 查询sku是否有库存
     *
     * @return
     */

    @Override
    public List<SkuHasStockTo> getSkuHasStock(List<Long> skuIds) {

        List<SkuHasStockTo> skuHasStockTos = skuIds.stream().map(id -> {
            SkuHasStockTo skuHasStockTo = new SkuHasStockTo();
            skuHasStockTo.setSkuId(id);

            Integer count = baseMapper.getTotalStock(id);
            skuHasStockTo.setHasStock(count == null ? false : count > 0);
            return skuHasStockTo;
        }).collect(Collectors.toList());
        return skuHasStockTos;
    }


    @Data
    class SkuLockVo {
        private Long skuId;
        private Integer num;
        private List<Long> wareIds;
    }

    /**
     * s锁库存
     * (rollbackFor = NoStockException.class)// 没有库存一定回滚
     *
     * @param wareSkuLockVo
     * @return
     */

    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVo wareSkuLockVo) {

        //因为可能出现订单回滚后，库存锁定不回滚的情况，但订单已经回滚，得不到库存锁定信息，因此要有库存工作单
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(wareSkuLockVo.getOrderSn());
        taskEntity.setCreateTime(new Date());
        wareOrderTaskService.save(taskEntity);

        List<OrderItemVo> itemVos = wareSkuLockVo.getLocks();

        List<SkuLockVo> lockVos = itemVos.stream().map((item) -> {
            SkuLockVo skuLockVo = new SkuLockVo();
            skuLockVo.setSkuId(item.getSkuId());
            skuLockVo.setNum(item.getCount());
            //找出所有库存大于商品数的仓库，直接锁定
            List<Long> wareIds = baseMapper.listWareIdsHasStock(item.getSkuId(), item.getCount());
            skuLockVo.setWareIds(wareIds);
            return skuLockVo;
        }).collect(Collectors.toList());

        // 待锁的商品、数量、仓库id信息
        for (SkuLockVo lockVo : lockVos) {
            boolean lock = true;
            Long skuId = lockVo.getSkuId();
            List<Long> wareIds = lockVo.getWareIds();
            //如果没有满足条件的仓库，抛出异常
            if (wareIds == null || wareIds.size() == 0) {
                throw new NoStockException(skuId);
            } else {
                for (Long wareId : wareIds) {
                    // 一个个仓库的锁定
                    // 成功返回1
                    Long count = baseMapper.lockWareSku(skuId, lockVo.getNum(), wareId);
                    if (count == 0) {
                        lock = false;
                    } else {
                        //1. 锁定成功，保存工作单详情到中间表
                        WareOrderTaskDetailEntity detailEntity = WareOrderTaskDetailEntity.builder()
                                .skuId(skuId)
                                .skuName("")
                                .skuNum(lockVo.getNum())
                                .taskId(taskEntity.getId())
                                .wareId(wareId)
                                .lockStatus(1).build();
                        wareOrderTaskDetailService.save(detailEntity);
                        //2. 发送库存锁定消息至延迟队列
                        StockLockedTo lockedTo = new StockLockedTo();
                        lockedTo.setId(taskEntity.getId());
                        StockDetailTo detailTo = new StockDetailTo();
                        try {
                            BeanUtils.copyProperties(detailEntity, detailTo);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        lockedTo.setDetailTo(detailTo);
                        rabbitTemplate.convertAndSend("stock-event-exchange", "stock.delay", lockedTo);

                        lock = true;
                        break;
                    }
                }
            }
            if (!lock) throw new NoStockException(skuId);
        }
        return true;
    }

    /**
     * 监听到了死信队列的信息（一定比订单状态发生改变的时间长)
     * 1、没有这个订单，必须解锁库存
     * 2、有这个订单，不一定解锁库存
     * *              订单状态：已取消：解锁库存
     * *                      已支付：不能解锁库存
     * 消息队列解锁库存
     *
     * @param stockLockedTo
     */
    @Override
    public void unlock(StockLockedTo stockLockedTo) {
        StockDetailTo detailTo = stockLockedTo.getDetailTo();
        WareOrderTaskDetailEntity detailEntity = wareOrderTaskDetailService.getById(detailTo.getId());
        //1.如果工作单详情不为空，说明该库存锁定成功
        if (detailEntity != null) {
            WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(stockLockedTo.getId());
            R r = orderFeignService.infoByOrderSn(taskEntity.getOrderSn());
            if (r.getCode() == 0) {
                OrderTo order = r.getData(new TypeReference<OrderTo>() {
                });
                //没有这个订单||订单状态已经取消 解锁库存
                if (order == null || order.getStatus() == OrderStatusEnum.CANCLED.getCode()) {
                    //为保证幂等性，只有当工作单详情处于被锁定的情况下才进行解锁
                    if (detailEntity.getLockStatus() == WareTaskStatusEnum.Locked.getCode()) {
                        unlockStock(detailTo.getSkuId(), detailTo.getSkuNum(), detailTo.getWareId(), detailEntity.getId());
                    }
                }
            } else {
                throw new RuntimeException("远程调用订单服务失败");
            }
        } else {
            //无需解锁，因为
        }
    }






    /**
     * 订单释放直接绑定库存解锁
     *
     * 防止订单服务卡顿，导致订单状态消息一在 库存解锁判断之后 才更新，一直无法解锁库存的问题出现
     * 导致网络原因卡顿的订单，后来即使取消了，但是库存一直无法解锁
     * @param orderTo
     */
    @Transactional
    @Override
    public void unlock(OrderTo orderTo) {
        //为防止重复解锁，需要重新查询工作单
        String orderSn = orderTo.getOrderSn();
        WareOrderTaskEntity taskEntity = wareOrderTaskService.getBaseMapper().selectOne((new QueryWrapper<WareOrderTaskEntity>().eq("order_sn", orderSn)));
        // 中间表
        //查询出当前订单相关的且处于锁定状态的工作单详情
        List<WareOrderTaskDetailEntity> lockDetails = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>().eq("task_id", taskEntity.getId()).eq("lock_status", WareTaskStatusEnum.Locked.getCode()));
        for (WareOrderTaskDetailEntity lockDetail : lockDetails) {
            unlockStock(lockDetail.getSkuId(), lockDetail.getSkuNum(), lockDetail.getWareId(), lockDetail.getId());
        }
    }

    /**
     * 封装的方法
     * @param skuId
     * @param skuNum
     * @param wareId
     * @param detailId
     */

    private void unlockStock(Long skuId, Integer skuNum, Long wareId, Long detailId) {
        //数据库中解锁库存数据
        baseMapper.unlockStock(skuId, skuNum, wareId);
        //更新库存工作单详情的状态
        WareOrderTaskDetailEntity detail = WareOrderTaskDetailEntity.builder()
                .id(detailId)
                .lockStatus(2).build();
        wareOrderTaskDetailService.updateById(detail);
    }

}
