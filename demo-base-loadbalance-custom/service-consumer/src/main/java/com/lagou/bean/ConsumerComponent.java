package com.lagou.bean;

import com.lagou.service.HelloService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

@Component
public class ConsumerComponent {

    //从远程，生成一个代理
    //引用dubbo组件
    @Reference(loadbalance = "onlyFirst")
    public HelloService helloService;

    public String sayHello(String name){
        return helloService.sayHello(name);
    }
}
