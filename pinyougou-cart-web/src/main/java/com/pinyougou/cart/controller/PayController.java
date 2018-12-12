package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 支付控制层
 *
 * @Author ll
 * @Date 2018/12/12 14:48
 */
@RestController
@RequestMapping("/pay")
public class PayController {
    @Autowired
    private IdWorker idWorker;

    @Reference
    private WeixinPayService weixinPayService;

    @RequestMapping("/createNative")
    public Map createNative() {
        String out_trade_no = idWorker.nextId() + "";
        return weixinPayService.createNative(out_trade_no, "1");
    }
}
