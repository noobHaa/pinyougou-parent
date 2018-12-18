package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.util.IdWorker;
import dto.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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
    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/createNative")
    public Map createNative() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //缓存中读取支付日志，生成二维码
        TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(name);
        if (seckillOrder != null) {
            return weixinPayService.createNative(seckillOrder.getId() + "", (long) (seckillOrder.getMoney().doubleValue() * 100) + "");
        } else {
            return new HashMap();
        }
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Result result = null;
        int x = 0;
        while (true) {//循环访问订单状态
            Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);
            if (map == null) {
                result = new Result(false, "支付出错");
                break;
            }
            if (map.get("trade_state").equals("SUCCESS")) {//如果成功,修改订单和支付日志的状态
                seckillOrderService.saveOrderFromRedisToDb(name, out_trade_no, map.get("transaction_id"));
                result = new Result(true, "支付成功");
                break;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("刷新了页面");
            x++;
            if (x > 4) {
                result = new Result(false, "二维码超时");
                //关闭支付
                Map<String, String> closePay = weixinPayService.closePay(out_trade_no);
                //判断是否支付完成，完成则存储到数据库
                if (!"SUCCESS".equals(closePay.get("result_code"))) {
                    if ("ORDERPAID".equals(closePay.get("err_code"))) {
                        seckillOrderService.saveOrderFromRedisToDb(name, out_trade_no, map.get("transaction_id"));
                        result = new Result(true, "支付成功");
                    }
                }
                if (result.getSuccess() == false) {
                    //取消订单
                    seckillOrderService.deleteOrderFromRedis(name, out_trade_no);
                }
                break;
            }
        }
        return result;
    }
}
