package com.pinyougou.search.service;

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
}
