package com.lagou.loadbalance;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;

public class OnlyFirstLoadBalance implements LoadBalance {
    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        //List<Invoker<T>> invokers 所有的服务提供者
        //所有的提供者 安装IP + 端口 排序 选择第一个

        Invoker<T> tInvoker = invokers.stream().sorted((i1, i2) -> {
            final int ipCompare = i1.getUrl().getIp().compareTo(i2.getUrl().getIp());

            if (ipCompare == 0) {
                return Integer.compare(i1.getUrl().getPort(), i2.getUrl().getPort());
            } else {
                return ipCompare;
            }
        }).findFirst().get();

        System.out.println("执行了负载均衡");

        return tInvoker;
    }
}
