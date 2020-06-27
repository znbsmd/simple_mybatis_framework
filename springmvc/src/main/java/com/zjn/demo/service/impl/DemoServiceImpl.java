package com.zjn.demo.service.impl;

import com.zjn.demo.service.IDemoService;
import com.zjn.mvcframework.annotations.MyService;

/**
 * @author: 190coder <190coder.cn>
 * @description:
 * @create: 2020-06-07 18:02
 */
@MyService("demoService")
public class DemoServiceImpl  implements IDemoService {
    @Override
    public String get(String name) {
        System.out.println("service 实现类中的name参数：" + name) ;
        return name;
    }
}
