package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

/**
 * @Author ll
 * @Date 2018/11/16 14:58
 */
public interface ItemSearchService {

    /**
     * 关键字搜索功能
     *
     * @param searchMap
     * @return
     */
    public Map<String, Object> search(Map searchMap);

    /**
     * 更新solr的商品
     *
     * @param itemList
     */
    public void importList(List itemList);

    /**
     * 删除solr的商品
     *
     * @param ids
     */
    public void delItemByGoodsId(List ids);
}
