package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.util.IdWorker;
import dto.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import vo.Cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service(timeout = 50000)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return orderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private TbPayLogMapper payLogMapper;

    /**
     * 增加
     */
    @Override
    public void add(TbOrder order) {
        //1根据order拿到用户id，取出购物车
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());

        //2遍历购物车，存放订单详情到订单明细表
        if (!cartList.isEmpty() && cartList.size() > 0) {
            double total_money = 0;//总金额（元）
            List<String> orderIdList = new ArrayList<>();
            for (Cart cart : cartList) {
                //3使用snowflake算法 生成唯一的主键id
                /*IdWorker idWorker=new IdWorker(0,0);*/
                long id = idWorker.nextId();
                TbOrder tbOrder = new TbOrder();
                tbOrder.setOrderId(id);//订单ID
                tbOrder.setUserId(order.getUserId());//用户名
                tbOrder.setPaymentType(order.getPaymentType());//支付类型
                tbOrder.setStatus("1");//状态：未付款
                tbOrder.setCreateTime(new Date());//订单创建日期
                tbOrder.setUpdateTime(new Date());//订单更新日期
                tbOrder.setReceiverAreaName(order.getReceiverAreaName());//地址
                tbOrder.setReceiverMobile(order.getReceiverMobile());//手机号
                tbOrder.setReceiver(order.getReceiver());//收货人
                tbOrder.setSourceType(order.getSourceType());//订单来源
                tbOrder.setSellerId(cart.getSellerId());//商家ID

                double money = 0;
                //循环购物车明细
                for (TbOrderItem orderItem : cart.getOrderItemList()) {
                    orderItem.setId(idWorker.nextId());
                    orderItem.setOrderId(id);
                    orderItem.setSellerId(cart.getSellerId());
                    orderItemMapper.insert(orderItem);
                    money += orderItem.getTotalFee().doubleValue();
                }
                tbOrder.setPayment(new BigDecimal(money));
                orderMapper.insert(tbOrder);
                orderIdList.add(id + "");
                total_money += money;
            }
            //判断订单的付款方式是否是微信支付，如果是微信支付需要将订单号，金额存放到redis用于生成二维码
            if ("1".equals(order.getPaymentType())) {
                TbPayLog payLog = new TbPayLog();
                payLog.setOutTradeNo(idWorker.nextId() + "");//支付订单号 日志表的主键
                payLog.setCreateTime(new Date());
                payLog.setUserId(order.getUserId());//用户id
                payLog.setTradeState("0");//支付状态
                payLog.setOrderList(orderIdList.toString().replace("[", "").replace("]", "").replace(" ", ""));//订单号集合
                payLog.setPayType("1");//支付类型
                payLog.setTotalFee((long) total_money * 100);

                payLogMapper.insert(payLog);
                redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);
            }
            //4清除redis中的购物车
            redisTemplate.boundHashOps("cartList").delete(order.getUserId());
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(TbOrder order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbOrder findOne(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            orderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();

        if (order != null) {
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
            }
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andPostFeeLike("%" + order.getPostFee() + "%");
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {
                criteria.andStatusLike("%" + order.getStatus() + "%");
            }
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andShippingNameLike("%" + order.getShippingName() + "%");
            }
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
            }
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + order.getUserId() + "%");
            }
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
            }
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
            }
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
            }
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
            }
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
            }
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {
                criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
            }
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + order.getSellerId() + "%");
            }

        }

        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public TbPayLog searchPayLogFromRedis(String userId) {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        //修改支付日志状态
        payLog.setPayTime(new Date());
        payLog.setTransactionId(transaction_id);//交易流水号
        payLog.setTradeState("1");

        payLogMapper.updateByPrimaryKey(payLog);
        //修改订单的支付状态
        String[] orderIds = payLog.getOrderList().split(",");
        for (String orderId : orderIds) {
            if (orderId != null) {
                TbOrder order = orderMapper.selectByPrimaryKey(Long.valueOf(orderId));
                order.setPaymentTime(new Date());
                order.setStatus("2");//已付款
                orderMapper.updateByPrimaryKey(order);
            }
        }
        //清除缓存中的日志
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }
}
