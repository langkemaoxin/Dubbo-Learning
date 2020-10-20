package com.lagou.service.impl;

import com.lagou.service.HelloService;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "hello---"+name;
    }
}
