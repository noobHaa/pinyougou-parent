package com.pinyougou.pay.service;

import java.util.Map;

/**
 * @Author ll
 * @Date 2018/12/12 14:25
 */
public interface WeixinPayService {
    /**
     * 生成支付的二维码
     *
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    public Map createNative(String out_trade_no, String total_fee);
}
