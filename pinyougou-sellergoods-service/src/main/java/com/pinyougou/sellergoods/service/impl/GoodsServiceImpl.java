package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;
import dto.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import vo.Goods;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service(timeout = 10000)
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbGoods goods) {
        goodsMapper.insert(goods);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbGoods goods) {
        goodsMapper.updateByPrimaryKey(goods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setTbGoods(tbGoods);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setTbGoodsDesc(tbGoodsDesc);

        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria().andGoodsIdEqualTo(id);
        List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);
        goods.setTbItems(tbItems);
        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            goodsMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();

        criteria.andIsDeleteIsNull();
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                //修改为精准查询
                //criteria.andSellerIdLike("%" + goods.getSellerId() + "%");
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbSellerMapper sellerMapper;

    @Override
    public void add(Goods goods) {
        goods.getTbGoods().setAuditStatus("0");
        goods.getTbGoods().setIsMarketable("0");
        goodsMapper.insert(goods.getTbGoods());

        TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
        tbGoodsDesc.setGoodsId(goods.getTbGoods().getId());
        goodsDescMapper.insert(tbGoodsDesc);
        saveItemList(goods);
    }

    public void saveItemList(Goods goods) {
        //判断是否启用规格，没启用就添加标准商品
        if (goods.getTbGoods().getIsEnableSpec().equals("1")) {
            for (TbItem item : goods.getTbItems()) {
                //sku存储 title  image  categoryId  createTime updateTime  goodsId  sellerId  category  brand  seller
                String title = goods.getTbGoods().getGoodsName();
                Map<String, Object> map = JSON.parseObject(item.getSpec());
                for (String key : map.keySet()) {
                    title += " " + map.get(key);
                }
                item.setTitle(title);
                setItemValus(item, goods);
                itemMapper.insert(item);
            }
        } else {
            TbItem item = new TbItem();
            item.setTitle(goods.getTbGoods().getGoodsName());//商品KPU+规格描述串作为SKU名称
            item.setPrice(goods.getTbGoods().getPrice());//价格
            item.setStatus("1");//状态
            item.setIsDefault("1");//是否默认
            item.setNum(99999);//库存数量
            item.setSpec("{}");
            setItemValus(item, goods);
            itemMapper.insert(item);
        }
    }

    public void setItemValus(TbItem item, Goods goods) {
        List<Map> list = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(), Map.class);
        if (list.size() > 0) {
            item.setImage((String) list.get(0).get("url"));
        }
        item.setCategoryid(goods.getTbGoods().getCategory3Id());
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        item.setGoodsId(goods.getTbGoods().getId());
        item.setSellerId(goods.getTbGoods().getSellerId());
        item.setCategory(itemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id()).getName());
        item.setBrand(brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId()).getName());
        item.setSeller(sellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId()).getNickName());
    }

    @Override
    public void update(Goods goods) {
        goodsMapper.updateByPrimaryKey(goods.getTbGoods());
        goodsDescMapper.updateByPrimaryKey(goods.getTbGoodsDesc());

        //删除原有的商品
        TbItemExample tbItemExample = new TbItemExample();
        tbItemExample.createCriteria().andGoodsIdEqualTo(goods.getTbGoods().getId());
        itemMapper.deleteByExample(tbItemExample);
        saveItemList(goods);

    }

    @Override
    public void updateStatus(Long[] ids, String auditStatus) {
        for (Long id : ids) {
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setAuditStatus(auditStatus);
            goodsMapper.updateByPrimaryKey(goods);
        }
    }

    @Override
    public void updateMarketStatus(Long[] ids, String isMarketable) {
        for (Long id : ids) {
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setIsMarketable(isMarketable);
            goodsMapper.updateByPrimaryKey(goods);
        }
    }

    @Override
    public List<TbItem> findTbItemListByGoodsIdAndStatus(Long[] ids, String status) {
        TbItemExample tbItemExample = new TbItemExample();
        tbItemExample.createCriteria().andGoodsIdIn(Arrays.asList(ids)).andStatusEqualTo(status);
        List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);
        return tbItems;
    }
}
