package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import dto.PageResult;

import java.util.List;

/**
 * 品牌服务层接口
 *
 * @Author ll
 * @Date 2018/10/30 11:18
 */
public interface BrandService {

    /**
     * 返回全部列表
     *
     * @return
     */
    public List<TbBrand> findAll();

    /**
     * 查询分页对象
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult findPage(int pageNum, int pageSize);
}
