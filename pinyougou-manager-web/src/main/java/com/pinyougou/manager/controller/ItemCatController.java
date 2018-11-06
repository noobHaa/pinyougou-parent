package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import dto.PageResult;
import dto.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.ItemCat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    @Reference
    private TypeTemplateService templateService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbItemCat> findAll() {
        return itemCatService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return itemCatService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param itemCat
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody ItemCat itemCat) {
        try {
            JSONObject jsonObject = (JSONObject) JSON.parse(itemCat.getTypeId());
            String id = jsonObject.getString("id");
            TbItemCat tbItemCat = itemCat.getTbItemCat();
            tbItemCat.setTypeId(Long.parseLong(id));
            itemCatService.add(tbItemCat);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param itemCat
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody ItemCat itemCat) {
        try {
            JSONObject jsonObject = (JSONObject) JSON.parse(itemCat.getTypeId());
            String id = jsonObject.getString("id");
            TbItemCat tbItemCat = itemCat.getTbItemCat();
            tbItemCat.setTypeId(Long.parseLong(id));
            itemCatService.update(tbItemCat);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public ItemCat findOne(Long id) {
        ItemCat itemCat = new ItemCat();
        TbItemCat one = itemCatService.findOne(id);
        itemCat.setTbItemCat(one);

        String typeName = templateService.findOne(one.getTypeId()).getName();
        Long typeId = templateService.findOne(one.getTypeId()).getId();
        Map<Object, Object> idMap = new HashMap<>();
        idMap.put("id", typeId);
        idMap.put("text", typeName);
        itemCat.setTypeId(JSON.toJSONString(idMap));
        return itemCat;
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            //不能删除有下级分类的
            for (Long id : ids) {
                List<TbItemCat> itemCats = findByParentId(id);
                if (!itemCats.isEmpty() && itemCats.size() > 0) {
                    throw new Exception("不能删除含有下级分类的属性");
                }
            }
            itemCatService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param itemCat
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbItemCat itemCat, int page, int rows) {
        return itemCatService.findPage(itemCat, page, rows);
    }

    /**
     * 查询级别目录
     *
     * @param parentId
     * @return
     */
    @RequestMapping("/findByParentId")
    public List<TbItemCat> findByParentId(Long parentId) {
        return itemCatService.findByParentId(parentId);
    }

}
