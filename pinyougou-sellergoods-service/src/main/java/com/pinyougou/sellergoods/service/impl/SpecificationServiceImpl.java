package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.sellergoods.service.SpecificationService;
import dto.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import vo.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service(timeout = 10000)
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;
    @Autowired
    private TbSpecificationOptionMapper optionMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Specification specification) {
        //增加数据
        specificationMapper.insert(specification.getSpecification());
        //没有规格的具体数据
        if (!specification.getSpecificationOptions().isEmpty() && specification.getSpecificationOptions().size() > 0) {
            for (TbSpecificationOption option : specification.getSpecificationOptions()) {
                option.setSpecId(specification.getSpecification().getId());
                optionMapper.insert(option);
            }
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(Specification specification) {
        specificationMapper.updateByPrimaryKey(specification.getSpecification());

        TbSpecificationOptionExample optionExample = new TbSpecificationOptionExample();
        optionExample.createCriteria().andSpecIdEqualTo(specification.getSpecification().getId());
        optionMapper.deleteByExample(optionExample);

        //如果为空，则表示将之前的全部删除
        if (!specification.getSpecificationOptions().isEmpty() && specification.getSpecificationOptions().size() > 0) {
            for (TbSpecificationOption option : specification.getSpecificationOptions()) {
                option.setSpecId(specification.getSpecification().getId());
                optionMapper.insert(option);
            }
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Specification findOne(Long id) {
        Specification specification = new Specification();
        specification.setSpecification(specificationMapper.selectByPrimaryKey(id));

        TbSpecificationOptionExample optionExample = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = optionExample.createCriteria().andSpecIdEqualTo(id);
        List<TbSpecificationOption> specificationOptions = optionMapper.selectByExample(optionExample);

        specification.setSpecificationOptions(specificationOptions);
        return specification;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        TbSpecificationOptionExample optionExample = new TbSpecificationOptionExample();
        for (Long id : ids) {
            specificationMapper.deleteByPrimaryKey(id);
            TbSpecificationOptionExample.Criteria criteria = optionExample.createCriteria().andSpecIdEqualTo(id);
            optionMapper.deleteByExample(optionExample);
        }
    }


    @Override
    public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSpecificationExample example = new TbSpecificationExample();
        Criteria criteria = example.createCriteria();

        if (specification != null) {
            if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }

        }

        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Map> findSpecificationList() {
        return specificationMapper.findSpecificationList();
    }

}
