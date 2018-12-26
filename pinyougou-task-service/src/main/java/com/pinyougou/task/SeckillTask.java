package com.pinyougou.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author lil
 * @data 2018/12/26
 */
@Component
public class SeckillTask {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /*任务调度：6-7个域，second minute hour dayOfmonth month dayOfweek (year)*/
    @Scheduled(cron = "* * * * * ?")
    public void refreshSeckillGoods() {
        /*查询缓存中存在的数据*/
        List<Long> ids = (List) redisTemplate.boundHashOps("seckillGoods").keys();
        /*查询数据库进行增量添加缓存*/
        TbSeckillGoodsExample seckillGoodsExample = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = seckillGoodsExample.createCriteria();
        //秒杀商品状态为已审核
        criteria.andStatusEqualTo("1");
        //秒杀的活动时间
        criteria.andStartTimeLessThanOrEqualTo(new Date());
        criteria.andEndTimeGreaterThanOrEqualTo(new Date());
        //剩余库存数
        criteria.andStockCountGreaterThan(0);

        criteria.andIdNotIn(ids);

        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(seckillGoodsExample);

        for (TbSeckillGoods seckillGoods : seckillGoodsList) {
            if (seckillGoods != null)
                //放入缓存
                redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
            System.out.println("增加添加到缓存中，id:" + seckillGoods.getId());
        }
    }

    @Scheduled(cron = "* * * * * ?")
    public void removeSeckillGoods() {
         /*查询缓存中存在的数据*/
        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
        for (TbSeckillGoods seckillGoods : seckillGoodsList) {
            //时间是否过期
            if (seckillGoods.getEndTime().getTime() >= (new Date()).getTime()) {
                continue;
            }
            //时间过期清除缓存，更新到数据库
            redisTemplate.boundHashOps("seckillGoods").delete(seckillGoods);

            seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
            System.out.println("移除缓存中的商品，id:" + seckillGoods.getId());
        }
    }
}
