package com.pinyougou.page.service;

/**
 * @Author ll
 * @Date 2018/11/22 15:40
 */
public interface ItemPageService {
    /**
     * 根据商品id生成静态页面
     *
     * @param goodsId
     * @return
     */
    public boolean genItemHtml(Long goodsId);
}
