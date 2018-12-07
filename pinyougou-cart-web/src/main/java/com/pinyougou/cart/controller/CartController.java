package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.util.CookieUtil;
import dto.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.Cart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author ll
 * @Date 2018/12/7 15:34
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Reference
    private CartService cartService;

    /**
     * 取出cookie中的购物车
     *
     * @return
     */
    @RequestMapping("findCartList")
    public List<Cart> findCartList() {
        String cartList = CookieUtil.getCookieValue(request, "cartList", "utf-8");
        if (cartList == null || cartList == "") {
            //如果购物车不存在
            cartList = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartList, Cart.class);//转换cookie中的购物车
        return cartList_cookie;
    }

    /**
     * 添加到购物车
     *
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num) {
        try {
            List<Cart> cartList = findCartList();
            List<Cart> carts = cartService.addGoodsToCartList(cartList, itemId, num);
            //购物车会填到cookie中
            CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(carts), 3600 * 24, "utf-8");
            return new Result(true, "购物车添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "购物车添加失败");
        }

    }
}
