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

//路径节点变化时，调用此方法
public class ReadyRestartInstances implements PathChildrenCacheListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadyRestartInstances.class);

    private static final String LISTEN_PATHS = "/lagou/dubbo/restart/instances";

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
            final ReadyRestartInstances instances=new ReadyRestartInstances(zooKeeperClient);

            //创建一个NodeCache对象
            PathChildrenCache nodeCache=new PathChildrenCache(zooKeeperClient,LISTEN_PATHS,false);

            //节点缓存对象加入监听
            nodeCache.getListenable().addListener(instances);

            try {
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
























