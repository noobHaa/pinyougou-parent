package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.util.CookieUtil;
import dto.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "utf-8");
        if (cartListString == null || cartListString == "") {
            //如果购物车不存在
            cartListString = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);//转换cookie中的购物车
        //判断用户是否登录
        if (username.equals("anonymousUser")) {//用户没有登录  从cookie中取
            return cartList_cookie;
        } else {//用户登录  从redis取
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            if (cartList_cookie.size() > 0) {//判断cookie中是否存在购物车
                //合并cookie和redis中的购物车
                List<Cart> cartList = cartService.mergeCartList(cartList_cookie, cartList_redis);
                //清空cookie
                CookieUtil.deleteCookie(request, response, "cartList");
                //将合并后的购物车存入缓存
                cartService.saveCartListToRedis(cartList, username);
                return cartList;
            }
            return cartList_redis;
        }
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
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //判断用户是否登录
            if (username.equals("anonymousUser")) {//没有登录保存到cookie
                //购物车会填到cookie中
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(carts), 3600 * 24, "utf-8");
            } else {//保存到redis
                cartService.saveCartListToRedis(carts, username);
            }
            return new Result(true, "购物车添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "购物车添加失败");
        }

    }
}
