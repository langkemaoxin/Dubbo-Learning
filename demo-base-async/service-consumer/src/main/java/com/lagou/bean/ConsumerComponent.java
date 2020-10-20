package com.lagou.bean;

import com.lagou.service.HelloService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

@Component
public class ConsumerComponent {

    //从远程，生成一个代理
    //引用dubbo组件
    @Reference
    public HelloService helloService;

    public String sayHello(String name,Integer waitTime){
        return helloService.sayHello(name,waitTime);
    }
}