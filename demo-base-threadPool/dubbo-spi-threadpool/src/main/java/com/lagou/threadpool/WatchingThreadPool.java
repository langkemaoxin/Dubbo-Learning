package com.lagou.threadpool;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.threadpool.support.fixed.FixedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

public class WatchingThreadPool extends FixedThreadPool implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(WatchingThreadPool.class);
    private static final double ALARM_PERCENT = 0.90;
    private final Map<URL, ThreadPoolExecutor> THREAD_POOLS = new ConcurrentHashMap<>();

    public WatchingThreadPool() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(this, 1, 3, TimeUnit.SECONDS);
    }

    @Override
    public Executor getExecutor(URL url) {
        final Executor executor = super.getExecutor(url);
        if (executor instanceof ThreadPoolExecutor) {
            THREAD_POOLS.put(url, ((ThreadPoolExecutor) executor));
        }
        return executor;
    }

    @Override
    public void run() {
        for (Map.Entry<URL, ThreadPoolExecutor> entry : THREAD_POOLS.entrySet()) {
            final URL url = entry.getKey();
            final ThreadPoolExecutor executor = entry.getValue();
            final int activeCount = executor.getActiveCount();
            final int poolSize = executor.getCorePoolSize();
            double used = (double) activeCount / poolSize;
            final int usedNum = (int) (used * 100);
            LOGGER.info("线程池执行状态:[{}/{}]:{}%", activeCount, poolSize, usedNum);

            if (used >= ALARM_PERCENT) {
                LOGGER.error("超出警戒值！host:{}, 当前已使用量:{}%, URL:{}", url.getIp(), usedNum, url);
            }
        }
    }
}