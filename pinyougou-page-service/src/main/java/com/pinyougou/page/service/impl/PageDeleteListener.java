package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**
 * @Author ll
 * @Date 2018/11/29 14:29
 */
@Component
public class PageDeleteListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Long [] goodsIds= (Long[]) objectMessage.getObject();
            boolean b = itemPageService.deleteItemHtml(goodsIds);
            System.out.println("删除静态化页面结果：" + b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
