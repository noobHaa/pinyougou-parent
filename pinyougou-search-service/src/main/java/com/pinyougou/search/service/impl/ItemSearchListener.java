package com.pinyougou.search.service.impl;


import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

/**
 * @Author ll
 * @Date 2018/11/27 9:54
 */
@Component
public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            System.out.println("监听到消息："+textMessage.getText());
            List<TbItem> items = JSON.parseArray(textMessage.getText(), TbItem.class);
            itemSearchService.importList(items);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
