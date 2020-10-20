package com.lagou;

import com.lagou.service.HelloService;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class ConsumerApplication {

    @Reference

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:dubbo-consumer.xml");

        HelloService helloService = context.getBean(HelloService.class);

        String result = helloService.sayHello("worl0001");

        System.out.println(result);


        System.in.read();
    }
}
