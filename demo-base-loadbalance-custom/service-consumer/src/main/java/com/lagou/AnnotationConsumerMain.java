package com.lagou;

import com.lagou.bean.ConsumerComponent;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

public class AnnotationConsumerMain {

    @Configuration
    @PropertySource("classpath:/dubbo-consumer.properties")
    @ComponentScan(basePackages = "com.lagou.bean")
    @EnableDubbo
    static class ConsumerConfiguration {
    }

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
        context.start();

        ConsumerComponent service = context.getBean(ConsumerComponent.class);

        System.out.println("启动了消费端...");

        while (true) {
            System.in.read();

            String world = service.sayHello("world");
            System.out.println(world);
        }

    }

}
