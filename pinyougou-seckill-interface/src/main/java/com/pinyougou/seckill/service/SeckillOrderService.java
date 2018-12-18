package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import dto.PageResult;

import java.util.List;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface SeckillOrderService {

    /**
     * 返回全部列表
     *
     * @return
     */
    public List<TbSeckillOrder> findAll();


    /**
     * 返回分页列表
     *
     * @return
     */
    public PageResult findPage(int pageNum, int pageSize);


    /**
     * 增加
     */
    public void add(TbSeckillOrder seckillOrder);


    /**
     * 修改
     */
    public void update(TbSeckillOrder seckillOrder);


    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    public TbSeckillOrder findOne(Long id);


    /**
     * 批量删除
     *
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 分页
     *
     * @param pageNum  当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize);

    /**
     * 抢购下单
     *
     * @param seckillId
     * @param userId
     */
    public void submitOrder(Long seckillId, String userId);

    /**
     * 查询缓存中的查询
     *
     * @param name
     * @return
     */
    public TbSeckillOrder searchOrderFromRedisByUserId(String name);

    /**
     * 支付成功，保存订单
     *
     * @param out_trade_no
     * @param transaction_id
     */
    public void saveOrderFromRedisToDb(String name, String out_trade_no, String transaction_id);

    /**
     * 取消订单
     *
     * @param name
     * @param out_trade_no
     */
    public void deleteOrderFromRedis(String name, String out_trade_no);
}
