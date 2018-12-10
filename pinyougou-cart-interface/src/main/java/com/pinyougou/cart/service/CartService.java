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

    /**
     * 通过username查询redis中的购物车
     *
     * @param username
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     * 保存购物车到redis中
     *
     * @param username
     */
    public void saveCartListToRedis(List<Cart> cartList, String username);

    /**
     * 合并购物车
     *
     * @param cartList1
     * @param cartList2
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2);
}
