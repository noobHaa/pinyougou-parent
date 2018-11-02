package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import dto.PageResult;

import java.util.List;
import java.util.Map;

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

    /**
     * 添加品牌
     *
     * @param brand
     */
    public void add(TbBrand brand);

    /**
     * 查询品牌
     *
     * @param id
     * @return
     */
    public TbBrand findOne(Long id);

    /**
     * 修改品牌
     *
     * @param brand
     */
    public void update(TbBrand brand);

    /**
     * 删除品牌
     *
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 查询品牌分页
     *
     * @param brand
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult findPage(TbBrand brand, int pageNum, int pageSize);

    /**
     * 获取所有品牌信息
     *
     * @return
     */
    public List<Map> findBrandList();
}
