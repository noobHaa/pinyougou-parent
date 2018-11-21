package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author ll
 * @Date 2018/11/16 15:08
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        //去掉关键字的空格，实现关键字的 或关系查询(例：三星手机 关键字为三星、手机，得到的结果会更多)
        if(searchMap.get("keywords")!=null){
            String keywords = ((String) searchMap.get("keywords")).replace(" ", "");
            searchMap.put("keywords", keywords);
        }
        System.out.println(searchMap.get("keywords"));
        Map<String, Object> map = new HashMap<>();
        //1、列表查询
        map.putAll(searchList(searchMap));
        //2、分组查询 分类查询
        map.put("categoryList", searchCategoryList(searchMap));
        //3.查询品牌和规格列表
        String categoryName = (String) searchMap.get("category");
        if (!"".equals(categoryName)) {//如果有分类名称
            map.putAll(searchBrandAndSpecList(categoryName));
        } else {//如果没有分类名称，按照第一个查询
            if (searchCategoryList(searchMap).size() > 0) {
                map.putAll(searchBrandAndSpecList(searchCategoryList(searchMap).get(0)));
            }
        }
        return map;
    }

    @Override
    public void importList(List itemList) {
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    @Override
    public void delItemByGoodsId(List ids) {
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_goodsid").in(ids);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 查询列表
     *
     * @param searchMap
     * @return
     */
    private Map searchList(Map searchMap) {
        Map<String, Object> map = new HashMap<>();
        //设置高亮
        HighlightQuery highlightQuery = new SimpleHighlightQuery();
        //1.1关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        highlightQuery.addCriteria(criteria);
        HighlightOptions highlightOptions = new HighlightOptions();
        //设置高亮的字段和样式
        highlightOptions.addField("item_title").setSimplePrefix("<em style='color:red'>").setSimplePostfix("</em>");
        highlightQuery.setHighlightOptions(highlightOptions);

        //1.2分类
        if (!"".equals(searchMap.get("category"))) {
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            highlightQuery.addFilterQuery(filterQuery);
        }
        //1.3按品牌筛选
        if (!"".equals(searchMap.get("brand"))) {
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            highlightQuery.addFilterQuery(filterQuery);
        }
        //1.4过滤规格
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                highlightQuery.addFilterQuery(filterQuery);
            }
        }
        //1.5价格过滤
        if (!"".equals(searchMap.get("price"))) {
            String[] priceStr = ((String) searchMap.get("price")).split("-");
            if (!priceStr[0].equals("0")) {
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(priceStr[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                highlightQuery.addFilterQuery(filterQuery);
            }
            if (!priceStr[1].equals("*")) {
                Criteria filterCriteria = new Criteria("item_price").lessThan(priceStr[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                highlightQuery.addFilterQuery(filterQuery);
            }
        }
        //1.6分页查询
        Integer pageNo = (Integer) searchMap.get("pageNo");
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageNo == null) {
            pageNo = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }
        highlightQuery.setOffset((pageNo - 1) * pageSize);//开始查询
        highlightQuery.setRows(pageSize);

        //1.7排序功能
        String sort = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");
        if (sortField != null && sortField != "") {
            if (sort.equals("DESC")) {
                Sort orders = new Sort(Sort.Direction.DESC, "item_" + sortField);
                highlightQuery.addSort(orders);
            }
            if (sort.equals("ASC")) {
                Sort orders = new Sort(Sort.Direction.ASC, "item_" + sortField);
                highlightQuery.addSort(orders);
            }
        }

        HighlightPage<TbItem> items = solrTemplate.queryForHighlightPage(highlightQuery, TbItem.class);

        List<HighlightEntry<TbItem>> highlighted = items.getHighlighted();
        for (HighlightEntry<TbItem> h : highlighted) {
            TbItem item = h.getEntity();
            if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) {
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
            }
        }
        map.put("rows", items.getContent());
        map.put("totalPage", items.getTotalPages());
        map.put("totalNum", items.getTotalElements());
        return map;
    }

    /**
     * 分组查询  分类
     *
     * @return
     */
    private List<String> searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList<>();
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);//where ..

        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);//group by ..

        GroupPage<TbItem> tbItems = solrTemplate.queryForGroupPage(query, TbItem.class);
        //所有入口
        Page<GroupEntry<TbItem>> entries = tbItems.getGroupResult("item_category").getGroupEntries();
        for (GroupEntry<TbItem> entry : entries) {
            list.add(entry.getGroupValue());
        }
        return list;
    }

    /**
     * 查询规格和品牌
     *
     * @param category
     * @return
     */
    private Map searchBrandAndSpecList(String category) {
        Map<String, Object> map = new HashMap<>();
        //获取缓存中的分类的模板id
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (typeId != null) {
            //获取品牌值
            List<Map> brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);
            //获取规格
            List<Map> specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }
        return map;
    }
}
