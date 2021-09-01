package henu.soft.xiaosi.cart.service.impl;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.TypeReference;
import henu.soft.common.constant.CartConstant;
import henu.soft.common.to.OrderItemTo;
import henu.soft.common.utils.R;
import henu.soft.xiaosi.cart.feign.ProductFeignService;
import henu.soft.xiaosi.cart.interceptor.CartInterceptor;
import henu.soft.xiaosi.cart.service.CartService;
import henu.soft.xiaosi.cart.to.UserInfoTo;
import henu.soft.xiaosi.cart.vo.CartItemVo;
import henu.soft.xiaosi.cart.vo.CartVo;
import henu.soft.common.to.SkuInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service("CartService")
@Slf4j
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public CartItemVo addCartItem(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> ops = getCartItemOps();
        // 判断当前商品是否已经存在购物车
        String cartJson = (String) ops.get(skuId.toString());
        // 1 已经存在购物车，将数据取出并添加商品数量
        if (!StringUtils.isEmpty(cartJson)) {
            //1.1 将json转为对象并将count+
            CartItemVo cartItemVo = JSON.parseObject(cartJson, CartItemVo.class);
            cartItemVo.setCount(cartItemVo.getCount() + num);
            //1.2 将更新后的对象转为json并存入redis
            String jsonString = JSON.toJSONString(cartItemVo);
            ops.put(skuId.toString(), jsonString);
            return cartItemVo;
        } else {
            CartItemVo cartItemVo = new CartItemVo();
            // 2 未存在购物车，则添加新商品
            CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
                //2.1 远程查询sku基本信息
                SkuInfoTo skuInfoTo = null;
                try {
                    R info = productFeignService.info(skuId);
                    TypeReference<SkuInfoTo> skuInfoToTypeReference = new TypeReference<>(){};
                    skuInfoTo = info.getData(skuInfoToTypeReference);
                    System.out.println(skuInfoTo+"======");
                    cartItemVo.setCheck(true);
                    cartItemVo.setCount(num);
                    cartItemVo.setImage(skuInfoTo.getSkuDefaultImg());
                    cartItemVo.setPrice(skuInfoTo.getPrice());
                    cartItemVo.setSkuId(skuId);
                    cartItemVo.setTitle(skuInfoTo.getSkuTitle());
                } catch (Exception e) {
                    log.error("远程调用失败:{}",e);
                    e.printStackTrace();
                }

            }, executor);

            //2.2 远程查询sku属性组合信息
            CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
                List<String> attrValuesAsString = productFeignService.getSkuSaleAttrValuesAsString(skuId);
                cartItemVo.setSkuAttrValues(attrValuesAsString);
            }, executor);

            try {
                CompletableFuture.allOf(future1, future2).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            //2.3 将该属性封装并存入redis,登录用户使用userId为key,否则使用user-key
            String toJSONString = JSON.toJSONString(cartItemVo);
            ops.put(skuId.toString(), toJSONString);
            return cartItemVo;
        }
    }

    @Override
    public CartItemVo getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartItemOps = getCartItemOps();
        String s = (String) cartItemOps.get(skuId.toString());
        CartItemVo cartItemVo = JSON.parseObject(s, CartItemVo.class);
        return cartItemVo;
    }

    @Override
    public CartVo getCart() {
        CartVo cartVo = new CartVo();

        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        //1 用户未登录，直接通过user-key获取临时购物车
        List<CartItemVo> tempCart = getCartByKey(userInfoTo.getUserKey());

        if (StringUtils.isEmpty(userInfoTo.getUserId())) {
            List<CartItemVo> cartItemVos = tempCart;
            cartVo.setItems(cartItemVos);
        } else {
            //2 用户登录
            //2.1 查询userId对应的购物车
            List<CartItemVo> userCart = getCartByKey(userInfoTo.getUserId().toString());
            //2.2 查询user-key对应的临时购物车，并和用户购物车合并

            if (tempCart != null && tempCart.size() > 0) {
                BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(CartConstant.CART_PREFIX + userInfoTo.getUserId());
                for (CartItemVo cartItemVo : tempCart) {
                    userCart.add(cartItemVo);
                    //2.3 在redis中更新数据
                    addCartItem(cartItemVo.getSkuId(), cartItemVo.getCount());
                }
            }
            cartVo.setItems(userCart);
            //2.4 删除临时购物车数据
            redisTemplate.delete(CartConstant.CART_PREFIX + userInfoTo.getUserKey());
        }

        return cartVo;
    }

    @Override
    public void checkCart(Long skuId, Boolean isChecked) {
        BoundHashOperations<String, Object, Object> ops = getCartItemOps();
        String cartJson = (String) ops.get(skuId.toString());
        CartItemVo cartItemVo = JSON.parseObject(cartJson, CartItemVo.class);
        cartItemVo.setCheck(isChecked);
        ops.put(skuId.toString(), JSON.toJSONString(cartItemVo));
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> ops = getCartItemOps();
        String cartJson = (String) ops.get(skuId.toString());
        CartItemVo cartItemVo = JSON.parseObject(cartJson, CartItemVo.class);
        cartItemVo.setCount(num);
        ops.put(skuId.toString(), JSON.toJSONString(cartItemVo));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> ops = getCartItemOps();
        ops.delete(skuId.toString());
    }


    /**
     * 查询购物车选中项
     * @return
     */
    @Override
    public List<OrderItemTo> getCheckedItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() == null) {
            System.out.println("没登录");
            //没登录
            return null;
        } else {
            //登录了
            System.out.println("登陆了");

            List<CartItemVo> cartByKey = getCartByKey(userInfoTo.getUserId().toString());

            List<OrderItemTo> list = new ArrayList<>();

            System.out.println("购物车选中了===》" + cartByKey);
            cartByKey.stream()
                    .filter(CartItemVo::getCheck)
                    .map((item)->{
                        OrderItemTo orderItemTo = new OrderItemTo();
                        BeanUtils.copyProperties(item,orderItemTo);
                        list.add(orderItemTo);
                        return item;
                    })
                    .collect(Collectors.toList());
            return list;
        }
    }




    private List<CartItemVo> getCartByKey(String userKey) {
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(CartConstant.CART_PREFIX + userKey);

        List<Object> values = ops.values();
        if (values != null && values.size() > 0) {
            List<CartItemVo> cartItemVos = values.stream().map(obj -> {
                String json = (String) obj;
                return JSON.parseObject(json, CartItemVo.class);
            }).collect(Collectors.toList());
            return cartItemVos;
        }
        return new ArrayList<CartItemVo>();
    }

    private BoundHashOperations<String, Object, Object> getCartItemOps() {
        //1判断是否已经登录
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        //1.1 登录使用userId操作redis
        if (!StringUtils.isEmpty(userInfoTo.getUserId())) {
            return redisTemplate.boundHashOps(CartConstant.CART_PREFIX + userInfoTo.getUserId());
        } else {
            //1.2 未登录使用user-key操作redis
            return redisTemplate.boundHashOps(CartConstant.CART_PREFIX + userInfoTo.getUserKey());
        }
    }
}
