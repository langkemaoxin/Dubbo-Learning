package com.lagou.router;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;

//预发布的路径管理器，用于缓存和监听所有的待灰度机器信息列表

/***
 * 如何构建此类？
 * 1. 写好所有必须的属性 监听路径，机器列表
 * 2. 如果当前类具有监听的功能，则需要实现相关的接口
 * 3. 写好构造函数，一般的工具类，都可以写一个静态构造函数，编程一个单例类，把相关的初始化逻辑，放在指定的创建方法中
 * 4. 对外暴露一个创建方法，写好初始化逻辑，如果当前实现了回调接口，则创建完对象后，对这个对象进行监听，并且返回
 * 5. 我们创建一个回调监听的接口实现类时，这个类就具有了回调功能，只要这个类不被销毁，则事件进行回调时，
 *    这个类中的，回调方法就会被调用
 */
public class ReadyRestartInstances implements PathChildrenCacheListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadyRestartInstances.class);

    private static final String LISTEN_PATHS = "/lagou/dubbo/restart/instances";

    //创建一个HashSet
    private Set<String> restartIntances = new HashSet<>();

    private final CuratorFramework zkClient;

    private ReadyRestartInstances(CuratorFramework client) {
        this.zkClient = client;
    }

    public static ReadyRestartInstances create() {
        final CuratorFramework zooKeeperClient = ZookeeperClients.client();

        Stat stat = null;
        try {
            stat = zooKeeperClient.checkExists().forPath(LISTEN_PATHS);

            if (stat == null) {
                zooKeeperClient.create().creatingParentsIfNeeded().forPath(LISTEN_PATHS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("确保基础路径存在！");
        }finally {

            //这里直接创建一个对象
            final ReadyRestartInstances instances=new ReadyRestartInstances(zooKeeperClient);

            //创建一个NodeCache监听对象
            PathChildrenCache nodeCache=new PathChildrenCache(zooKeeperClient,LISTEN_PATHS,false);

            //节点缓存对象加入监听
            nodeCache.getListenable().addListener(instances);

            try {
                //开始监听
                nodeCache.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return instances;
        }
    }


    //返回应用名和主机拼接后的字符串
    private String buildApplicationInstancsString(String applicationaname, String host) {
        return applicationaname + "_" + host;
    }

    //增加重启实例的配置信息方法
    public void addRestartingInstance(String applicationName, String host) throws Exception {
        zkClient.create().creatingParentsIfNeeded().forPath(LISTEN_PATHS + "/" + buildApplicationInstancsString(applicationName, host));
    }

    //删除重启实例的配置信息方法
    public void removeRestartingInstance(String applicationName, String host) throws Exception {
        zkClient.delete().forPath(LISTEN_PATHS + "/" + buildApplicationInstancsString(applicationName, host));
    }

    //判断节点信息是否存在于列表中
    public Boolean hasRestartingInstance(String applicationName, String host) throws Exception {
        return restartIntances.contains(buildApplicationInstancsString(applicationName, host));
    }

    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {

        //查询出监听路径上所有的配置目录信息
        final List<String> restartingInstances = zkClient.getChildren().forPath(LISTEN_PATHS);

        if (CollectionUtils.isEmpty(restartingInstances)) {
            this.restartIntances = Collections.emptySet();
        } else {
            this.restartIntances = new HashSet<>(restartingInstances);
        }
    }
}
























