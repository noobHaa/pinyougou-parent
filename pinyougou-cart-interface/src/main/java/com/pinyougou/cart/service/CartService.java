package com.pinyougou.cart.service;

import vo.Cart;

import java.util.List;

/**
 * @Author ll
 * @Date 2018/12/7 14:49
 */
public interface CartService {

    /**
     * 添加商品到购物车
     *
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);
}
