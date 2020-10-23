package com.lagou.router;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

//单例模式，构建zookeeper客户端
public class ZookeeperClients {

    private final CuratorFramework client;
    private static ZookeeperClients INSTANCE;

    static {
        //构建一个重试zookeeper客户端重试策略
        RetryPolicy retryPolicy=new ExponentialBackoffRetry(1000,3);

        //创建一个Curator Zookeeper 客户端对象
        CuratorFramework client= CuratorFrameworkFactory.newClient("81.68.247.80:2181",retryPolicy);

        //把这个Zookeeper对象作为参数出传入到 构造函数中
        INSTANCE=new ZookeeperClients(client);

        //对象创建后，开启Zookeeper连接对象
        client.start();
    }

    private ZookeeperClients(CuratorFramework client){
        this.client=client;

    }

    public static CuratorFramework client(){
        return client();
    }


}
