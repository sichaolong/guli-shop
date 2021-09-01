package henu.soft.xiaosi.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import henu.soft.common.constant.CartConstant;
import henu.soft.common.constant.OrderConstant;
import henu.soft.common.exception.NoStockException;
import henu.soft.common.to.MemberResponseTo;
import henu.soft.common.to.SkuHasStockTo;
import henu.soft.common.to.mq.OrderTo;
import henu.soft.common.to.mq.SecKillOrderTo;
import henu.soft.common.utils.R;
import henu.soft.xiaosi.order.entity.OrderItemEntity;
import henu.soft.xiaosi.order.entity.PaymentInfoEntity;
import henu.soft.xiaosi.order.enume.OrderStatusEnume;
import henu.soft.xiaosi.order.feign.CartFeignService;
import henu.soft.xiaosi.order.feign.MemberFeignService;
import henu.soft.xiaosi.order.feign.ProductFeignService;
import henu.soft.xiaosi.order.feign.WareFeignService;
import henu.soft.xiaosi.order.interceptor.LoginInterceptor;
import henu.soft.xiaosi.order.service.OrderItemService;
import henu.soft.xiaosi.order.service.PaymentInfoService;
import henu.soft.xiaosi.order.to.OrderCreateTo;
import henu.soft.xiaosi.order.to.SpuInfoTo;
import henu.soft.xiaosi.order.vo.*;
import henu.soft.common.to.OrderItemTo;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.utils.Query;

import henu.soft.xiaosi.order.dao.OrderDao;
import henu.soft.xiaosi.order.entity.OrderEntity;
import henu.soft.xiaosi.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {


    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    StringRedisTemplate redisTemplate;


    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    WareFeignService wareFeignService;


    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    ProductFeignService productFeignService;


    @Autowired
    OrderItemService orderItemService;



    @Autowired
    PaymentInfoService paymentInfoService;



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 点击结算购物商品去订单详情页
     *
     * @return
     */
    @Override
    public OrderConfirmVo confirmOrder() {
        // 需要给每个异步线程从新设置 主线程的ThreadLocal 共享数据
        MemberResponseTo memberResponseTo = LoginInterceptor.loginUser.get();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();


        // 待返回内容
        OrderConfirmVo confirmVo = new OrderConfirmVo();

        CompletableFuture<Void> itemAndStockFuture = CompletableFuture.supplyAsync(() -> {
            // 给每个异步线程设置cookie

            RequestContextHolder.setRequestAttributes(requestAttributes);


            //1. 查出所有选中购物项
            List<OrderItemTo> checkedItems = cartFeignService.getCheckedItems();
            confirmVo.setItems(checkedItems);
            return checkedItems;
        }, executor).thenAcceptAsync((items) -> {
            //4. 库存
            List<Long> skuIds = items.stream().map(OrderItemTo::getSkuId).collect(Collectors.toList());
            Map<Long, Boolean> hasStockMap = wareFeignService.getSkuHasStocks(skuIds).stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::getHasStock));
            confirmVo.setStocks(hasStockMap);
        }, executor);

        //2. 查出所有收货地址
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            List<MemberAddressVo> addressByUserId = memberFeignService.getAddressByUserId(memberResponseTo.getId());
            confirmVo.setMemberAddressVos(addressByUserId);
        }, executor);

        //3. 积分
        confirmVo.setIntegration(memberResponseTo.getIntegration());

        //5. 总价自动计算
        //6. 防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberResponseTo.getId(), token, 30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);
        try {
            CompletableFuture.allOf(itemAndStockFuture, addressFuture).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return confirmVo;

    }


    /**
     * 提交订单
     * 由于高并发 不使用 Seata的 @GlobalTransactional AT分布式事务，使用RabbitMQ的延时队列
     *
     * @param submitVo
     * @return
     */

    //@GlobalTransactional
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo submitVo) {
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        responseVo.setCode(0);
        //1. 验证防重令牌
        MemberResponseTo memberResponseTo = LoginInterceptor.loginUser.get();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long execute = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberResponseTo.getId()), submitVo.getOrderToken());
        if (execute == 0L) {
            //1.1 防重令牌验证失败
            responseVo.setCode(1);
            return responseVo;
        } else {
            //2. 创建订单、订单项
            OrderCreateTo order = createOrderTo(memberResponseTo, submitVo);

            //3. 验价
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = submitVo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                //4. 保存订单
                saveOrder(order);
                //5. 锁定库存,拿到要锁定的信息

                // TODO 远程锁库存
                List<OrderItemTo> orderItemTos = order.getOrderItems().stream().map((item) -> {
                    OrderItemTo orderItemTo = new OrderItemTo();
                    orderItemTo.setSkuId(item.getSkuId());
                    orderItemTo.setCount(item.getSkuQuantity());
                    return orderItemTo;
                }).collect(Collectors.toList());

                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                lockVo.setLocks(orderItemTos);
                R r = wareFeignService.orderLockStock(lockVo);
                //5.1 锁定库存成功
                if (r.getCode() == 0) {
//                    int i = 10 / 0;
                    responseVo.setOrder(order.getOrder());
                    responseVo.setCode(0);

                    // TODO 延时队列
                    //发送消息到订单延迟队列，判断过期订单
                    rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrder());

                    //清除购物车记录
                    BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(CartConstant.CART_PREFIX + memberResponseTo.getId());
                    for (OrderItemEntity orderItem : order.getOrderItems()) {
                        ops.delete(orderItem.getSkuId().toString());
                    }
                    return responseVo;
                } else {
                    //5.1 锁定库存失败，本地事务
                    String msg = (String) r.get("msg");
                    throw new NoStockException(msg);
                }

            } else {
                //验价失败
                responseVo.setCode(2);
                return responseVo;
            }
        }
    }




    /**
     * //4. 保存订单
     * @param orderCreateTo
     */

    private void saveOrder(OrderCreateTo orderCreateTo) {
        OrderEntity order = orderCreateTo.getOrder();
        order.setCreateTime(new Date());
        order.setModifyTime(new Date());
        this.save(order);
        orderItemService.saveBatch(orderCreateTo.getOrderItems());
    }

    /**
     * //2. 创建订单、订单项,生成订单号
     * @param memberResponseVo
     * @param submitVo
     * @return
     */
    private OrderCreateTo createOrderTo(MemberResponseTo memberResponseVo, OrderSubmitVo submitVo) {
        //用IdWorker生成订单号
        String orderSn = IdWorker.getTimeId();
        //构建订单,用户信息、邮费、状态
        OrderEntity entity = buildOrder(memberResponseVo, submitVo,orderSn);
        //构建订单项
        List<OrderItemEntity> orderItemEntities = buildOrderItems(orderSn);
        //计算价格
        compute(entity, orderItemEntities);
        OrderCreateTo createTo = new OrderCreateTo();
        createTo.setOrder(entity);
        createTo.setOrderItems(orderItemEntities);
        return createTo;
    }


    // 计算价格
    private void compute(OrderEntity entity, List<OrderItemEntity> orderItemEntities) {
        //总价
        BigDecimal total = BigDecimal.ZERO;
        //优惠价格
        BigDecimal promotion=new BigDecimal("0.0");
        BigDecimal integration=new BigDecimal("0.0");
        BigDecimal coupon=new BigDecimal("0.0");
        //积分
        Integer integrationTotal = 0;
        Integer growthTotal = 0;

        for (OrderItemEntity orderItemEntity : orderItemEntities) {
            total=total.add(orderItemEntity.getRealAmount());
            promotion=promotion.add(orderItemEntity.getPromotionAmount());
            integration=integration.add(orderItemEntity.getIntegrationAmount());
            coupon=coupon.add(orderItemEntity.getCouponAmount());
            integrationTotal += orderItemEntity.getGiftIntegration();
            growthTotal += orderItemEntity.getGiftGrowth();
        }

        entity.setTotalAmount(total);
        entity.setPromotionAmount(promotion);
        entity.setIntegrationAmount(integration);
        entity.setCouponAmount(coupon);
        entity.setIntegration(integrationTotal);
        entity.setGrowth(growthTotal);

        //付款价格=商品价格+运费
        entity.setPayAmount(entity.getFreightAmount().add(total));

        //设置删除状态(0-未删除，1-已删除)
        entity.setDeleteStatus(0);
    }

    //订单全部商品信息
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        List<OrderItemTo> checkedItems = cartFeignService.getCheckedItems();
        List<OrderItemEntity> orderItemEntities = checkedItems.stream().map((item) -> {
            // 每一个订单项信息进行封装
            OrderItemEntity orderItemEntity = buildOrderItem(item);
            //1) 设置订单号
            orderItemEntity.setOrderSn(orderSn);
            return orderItemEntity;
        }).collect(Collectors.toList());
        return orderItemEntities;
    }

    //每个订单商品信息,属性，实际价格

    private OrderItemEntity buildOrderItem(OrderItemTo item) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        Long skuId = item.getSkuId();
        //2) 设置sku相关属性
        orderItemEntity.setSkuId(skuId);
        orderItemEntity.setSkuName(item.getTitle());
        orderItemEntity.setSkuAttrsVals(StringUtils.collectionToDelimitedString(item.getSkuAttrValues(), ";"));
        orderItemEntity.setSkuPic(item.getImage());
        orderItemEntity.setSkuPrice(item.getPrice());
        orderItemEntity.setSkuQuantity(item.getCount());
        //3) 通过skuId查询spu相关属性并设置
        R r = productFeignService.getSpuBySkuId(skuId);
        if (r.getCode() == 0) {
            SpuInfoTo spuInfo = r.getData(new TypeReference<SpuInfoTo>() {
            });
            orderItemEntity.setSpuId(spuInfo.getId());
            orderItemEntity.setSpuName(spuInfo.getSpuName());
            orderItemEntity.setSpuBrand(spuInfo.getBrandName());
            orderItemEntity.setCategoryId(spuInfo.getCatalogId());
        }
        //4) 商品的优惠信息(不做)

        //5) 商品的积分成长，为价格x数量
        orderItemEntity.setGiftGrowth(item.getPrice().multiply(new BigDecimal(item.getCount())).intValue());
        orderItemEntity.setGiftIntegration(item.getPrice().multiply(new BigDecimal(item.getCount())).intValue());

        //6) 订单项订单价格信息
        orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
        orderItemEntity.setCouponAmount(BigDecimal.ZERO);
        orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);

        //7) 实际价格
        BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity()));
        BigDecimal realPrice = origin.subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(realPrice);

        return orderItemEntity;
    }

    //构建订单,用户信息、邮费、状态

    private OrderEntity buildOrder(MemberResponseTo memberResponseVo, OrderSubmitVo submitVo, String orderSn) {

        OrderEntity orderEntity =new OrderEntity();

        orderEntity.setOrderSn(orderSn);

        //2) 设置用户信息
        orderEntity.setMemberId(memberResponseVo.getId());
        orderEntity.setMemberUsername(memberResponseVo.getUsername());

        //3) 获取邮费和收件人信息并设置
        FareVo fareVo = wareFeignService.getFare(submitVo.getAddrId());
        BigDecimal fare = fareVo.getFare();
        orderEntity.setFreightAmount(fare);
        MemberAddressVo address = fareVo.getAddress();
        orderEntity.setReceiverName(address.getName());
        orderEntity.setReceiverPhone(address.getPhone());
        orderEntity.setReceiverPostCode(address.getPostCode());
        orderEntity.setReceiverProvince(address.getProvince());
        orderEntity.setReceiverCity(address.getCity());
        orderEntity.setReceiverRegion(address.getRegion());
        orderEntity.setReceiverDetailAddress(address.getDetailAddress());

        //4) 设置订单相关的状态信息
        orderEntity.setStatus(OrderStatusEnume.CREATE_NEW.getCode());
        orderEntity.setConfirmStatus(0);
        orderEntity.setAutoConfirmDay(7);

        return orderEntity;
    }


    /**
     * 库存回滚，查询订单信息
     * @param orderSn
     * @return
     */
    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity order_sn = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));

        return order_sn;
    }


    /**
     * 收到过期的订单信息，准备关闭订单
     * @param orderEntity
     */
    /**
     * 关闭过期的的订单
     * @param orderEntity
     */
    @Override
    public void closeOrder(OrderEntity orderEntity)  {
        //因为消息发送过来的订单已经是很久前的了，中间可能被改动，因此要查询最新的订单
        OrderEntity newOrderEntity = this.getById(orderEntity.getId());
        //如果订单还处于新创建的状态，说明超时未支付，进行关单
        if (newOrderEntity.getStatus() == OrderStatusEnume.CREATE_NEW.getCode()) {
            OrderEntity updateOrder = new OrderEntity();
            updateOrder.setId(newOrderEntity.getId());
            updateOrder.setStatus(OrderStatusEnume.CANCLED.getCode());
            this.updateById(updateOrder);

            //关单后发送消息通知其他服务进行关单相关的操作，如解锁库存
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(newOrderEntity,orderTo);

            // TODO 保证消息的可靠性，做好日志记录，可以在mysql包保存日志，定期扫描数据库，将失败的信息重新发送
            try {
                rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other",orderTo);
            } catch (AmqpException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 订单支付，查询待支付的订单信息
     * @param orderSn
     * @return
     */
    @Override
    public PayVo getOrderPay(String orderSn) {
        OrderEntity orderEntity = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        PayVo payVo = new PayVo();
        payVo.setOut_trade_no(orderSn);
        BigDecimal payAmount = orderEntity.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(payAmount.toString());

        List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        OrderItemEntity orderItemEntity = orderItemEntities.get(0);
        payVo.setSubject(orderItemEntity.getSkuName());
        payVo.setBody(orderItemEntity.getSkuAttrsVals());
        return payVo;
    }

    /**
     * 订单支付完成跳转订单列表，查询订单列表
     * @param params
     * @return
     */

    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {

        MemberResponseTo memberResVo = LoginInterceptor.loginUser.get();

        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<>();
        //降序排列
        wrapper.eq("member_id", memberResVo.getId()).orderByDesc("id");
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                wrapper
        );

        List<OrderEntity> orderEntities = page.getRecords().stream().map((orderEntity) -> {
            List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderEntity.getOrderSn()));
            orderEntity.setOrderItemEntities(orderItemEntities);
            return orderEntity;
        }).collect(Collectors.toList());

        //重新设置返回数据
        page.setRecords(orderEntities);

        return new PageUtils(page);
    }


    /**
     * 处理支付宝返回的数据
     * <p>
     * 只要我们收到了，支付宝给我们的一步的通知，告诉我订单支付成功
     * 返回success，支付宝就再也不通知
     */

    /**
     * 修改订单状态
     * @param payAsyncVo
     */
    @Override
    public void handlerPayResult(PayAsyncVo payAsyncVo) {
        //1.保存交易流水这个对象 PaymentInfoEntity
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setAlipayTradeNo(payAsyncVo.getTrade_no());
        paymentInfoEntity.setOrderSn(payAsyncVo.getOut_trade_no());//修改数据库为唯一属性
        paymentInfoEntity.setPaymentStatus(payAsyncVo.getTrade_status());
        paymentInfoEntity.setCallbackTime(payAsyncVo.getNotify_time());
        paymentInfoService.save(paymentInfoEntity);

        //2。修改订单状态
        if (payAsyncVo.getTrade_status().equals("TRADE_SUCCESS") || payAsyncVo.getTrade_status().equals("TRADE_FINISHED")) {
            //支付成功
            String outTradeNo = payAsyncVo.getOut_trade_no();
            this.baseMapper.updateOrderStatus(outTradeNo, OrderStatusEnume.PAYED.getCode());
        }

    }


    /**
     * 保存秒杀订单信息
     * @param
     */
    @Override
    public void createSeckillOrder(SecKillOrderTo secKillOrderTo) {

        log.info("\n创建秒杀订单");
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(secKillOrderTo.getOrderSn());
        entity.setMemberId(secKillOrderTo.getMemberId());
        entity.setCreateTime(new Date());
        entity.setPayAmount(secKillOrderTo.getSeckillPrice());
        entity.setTotalAmount(secKillOrderTo.getSeckillPrice());
        entity.setStatus(OrderStatusEnume.CREATE_NEW.getCode());
        entity.setPayType(1);
        // TODO 还有挺多的没设置
        BigDecimal price = secKillOrderTo.getSeckillPrice().multiply(new BigDecimal("" + secKillOrderTo.getNum()));
        entity.setPayAmount(price);

        this.save(entity);

        // 保存订单项信息
        OrderItemEntity itemEntity = new OrderItemEntity();
        itemEntity.setOrderSn(secKillOrderTo.getOrderSn());
        itemEntity.setRealAmount(price);
        itemEntity.setOrderId(entity.getId());
        itemEntity.setSkuQuantity(secKillOrderTo.getNum());
        R info = productFeignService.info(secKillOrderTo.getSkuId());
        SpuInfoTo spuInfo = info.getData(new TypeReference<SpuInfoTo>() {});
        itemEntity.setSpuId(spuInfo.getId());
        itemEntity.setSpuBrand(spuInfo.getBrandId().toString());
        itemEntity.setSpuName(spuInfo.getSpuName());
        itemEntity.setCategoryId(spuInfo.getCatalogId());
        itemEntity.setGiftGrowth(secKillOrderTo.getSeckillPrice().multiply(new BigDecimal(secKillOrderTo.getNum())).intValue());
        itemEntity.setGiftIntegration(secKillOrderTo.getSeckillPrice().multiply(new BigDecimal(secKillOrderTo.getNum())).intValue());
        itemEntity.setPromotionAmount(new BigDecimal("0.0"));
        itemEntity.setCouponAmount(new BigDecimal("0.0"));
        itemEntity.setIntegrationAmount(new BigDecimal("0.0"));
        orderItemService.save(itemEntity);
    }


}
