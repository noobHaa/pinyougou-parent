package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.util.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author ll
 * @Date 2018/12/12 14:29
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;

    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //1封装参数
        Map param = new HashMap();
        param.put("appid", appid);//公众账号id
        param.put("mch_id", partner);//商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("body", "品优购");//商品描述
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("total_fee", total_fee);//订单价格(分)
        param.put("spbill_create_ip", "127.0.0.1");//终端ip
        param.put("notify_url", "http://www.baidu.cn");//通知地址
        param.put("trade_type", "NATIVE");//交易类型

        try {
            String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println(signedXml);
            //2调用HttpClient得到返回值
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);//是否是https协议
            httpClient.setXmlParam(signedXml);//发送的xml数据
            httpClient.post();//执行post的请求

            String result = httpClient.getContent();//获取请求后返回的结果xml
            System.out.println(result);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);

            //3设置订单和价格的回显
            Map map = new HashMap();
            map.put("code_url", resultMap.get("code_url"));//支付地址
            map.put("out_trade_no", out_trade_no);
            map.put("total_fee", total_fee);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        //1封装参数
        Map param=new HashMap();
        param.put("appid", appid);//公众账号ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        String url="https://api.mch.weixin.qq.com/pay/orderquery";
        try {
            //2请求网页
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client=new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            //3返回结果
            String result = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println(map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
