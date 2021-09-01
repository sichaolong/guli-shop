package henu.soft.xiaosi.cart.service;


import henu.soft.common.to.OrderItemTo;
import henu.soft.xiaosi.cart.vo.CartItemVo;
import henu.soft.xiaosi.cart.vo.CartVo;

import java.util.List;

public interface CartService {
    CartItemVo addCartItem(Long skuId, Integer num);

    CartItemVo getCartItem(Long skuId);

    CartVo getCart();

    void checkCart(Long skuId, Boolean isChecked);

    void changeItemCount(Long skuId, Integer num);

    void deleteItem(Long skuId);

    List<OrderItemTo> getCheckedItems();
}
