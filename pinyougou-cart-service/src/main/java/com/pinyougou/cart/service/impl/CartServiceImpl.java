package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import vo.Cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author ll
 * @Date 2018/12/7 14:51
 */
@Service(timeout = 50000)
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据sku id查询出商家信息
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        String sellerId = tbItem.getSellerId();
        //判断商品是否存在
        if (tbItem == null) {
            throw new RuntimeException("商品不存在");
        }
        //判断商品状态是否正确  考虑时间差
        if (!tbItem.getStatus().equals("1")) {
            throw new RuntimeException("商品状态不合法");
        }
        //2.查询购物车中是否存在商家的购物车
        Cart cart = searchCartBySellerId(cartList, sellerId);

        if (cart == null) {
            //3.不存在购物车
            //需要创建商家购物车，添加到总购物车
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(tbItem.getSeller());
            //添加商家购物车明细
            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(tbItem, num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            //添加到总购物车
            cartList.add(cart);
        } else {
            //4.存在购物车
            //判断购物车中是否存在itemId商品
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            if (orderItem == null) {
                //5 .不存在商品
                //创建购物车商品实体，添加到商家购物车
                orderItem = createOrderItem(tbItem, num);
                cart.getOrderItemList().add(orderItem);
            } else {
                //6 .存在商品
                //追加商品实体数量和价格
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));

                //购物车页面可以操作商品的数量加减   num数量可能为负数
                if (orderItem.getNum() <= 0) {
                    cart.getOrderItemList().remove(orderItem);//商品数量为0  删除明细
                }
                if (cart.getOrderItemList().size() <= 0) {
                    cartList.remove(cart);//商家购物车为空  删除购物车
                }
            }
        }
        return cartList;
    }

    /**
     * 查询总的购物车中是否存在商家购物车
     *
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    /**
     * 查询商家购物车中是否存在商品
     *
     * @param cartList
     * @param sellerId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue() == itemId) {
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 创建购物车明细对象
     *
     * @param tbItem
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem tbItem, Integer num) {
        if (num <= 0) {
            throw new RuntimeException("数量不合法");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(tbItem.getGoodsId());
        orderItem.setItemId(tbItem.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(tbItem.getImage());
        orderItem.setPrice(tbItem.getPrice());
        orderItem.setSellerId(tbItem.getSellerId());
        orderItem.setTitle(tbItem.getTitle());
        orderItem.setTotalFee(new BigDecimal(tbItem.getPrice().doubleValue() * num));
        return orderItem;
    }
}
