package com;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author ll
 * @Date 2018/11/6 14:48
 */
public class Test {
    public static void main(String[] args) {
        Map<Object, Object> idMap = new HashMap<>();
        idMap.put("id",1);
        idMap.put("text","xiaomnin");
        System.out.println( idMap.toString());
    }
}
