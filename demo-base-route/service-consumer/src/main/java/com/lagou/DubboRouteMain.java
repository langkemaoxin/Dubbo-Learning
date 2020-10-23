package com.lagou;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.RegistryFactory;

public class DubboRouteMain {
    public static void main(String[] args) {

        RegistryFactory registryFactory= ExtensionLoader.getExtensionLoader(RegistryFactory.class).getAdaptiveExtension();

        Registry registry = registryFactory.getRegistry(URL.valueOf("zookeeper://81.68.247.80:2181"));

        registry.register(URL.valueOf("condition://0.0.0.0/com.lagou.service.HelloService?category=routers&force=true&dynamic=true&rule="
        + URL.encode("=>host!=192.168.52.2")));




    }
}
