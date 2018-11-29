package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @Author ll
 * @Date 2018/11/29 9:24
 */
@Component
public class PageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            Long id = Long.parseLong(textMessage.getText());
            boolean b = itemPageService.genItemHtml(id);
            System.out.println("静态化页面结果：" + b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
