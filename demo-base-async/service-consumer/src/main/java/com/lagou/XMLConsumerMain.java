package com.lagou;

import com.lagou.bean.ConsumerComponent;
import com.lagou.service.HelloService;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class XMLConsumerMain {


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("consumer.xml");

        HelloService service = context.getBean(HelloService.class);

        System.out.println("启动了消费端...");

        while (true) {
            System.in.read();

            String world = service.sayHello("world", 100);


            //利用future模式 来获取
            Future<Object> future= RpcContext.getContext().getFuture();

            System.out.println(world);

            System.out.println("future:"+future.get());
        }

    }

}
