package com.test.lock.distributelock.zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liugang
 * @Date: 2018/6/13 16:43
 */
public class CuratorDistriLock extends Thread implements Closeable {

    private CuratorFramework client;

    private String name;

    public CuratorDistriLock(String name) {
        this.name = name;
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                /**
                 * 会话超时时间
                 */
                .sessionTimeoutMs(5000)
                /**
                 * 连接超时时间
                 */
                .connectionTimeoutMs(5000)
                /**
                 * 重试策略
                 */
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        /**
         * 若创建节点的父节点不存在会先创建父节点再创建子节点
         */
        try {

            Stat stat = client.checkExists().forPath("/lock/curator_distri_lock");
            if (null != stat) {
                client.delete().forPath("/lock/curator_distri_lock");

            }
            String path = client.create().creatingParentsIfNeeded()
                    /**
                     * withMode节点类型
                     */
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)

                    .forPath("/lock/curator_distri_lock", "131".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            this.lock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lock() throws Exception {
        InterProcessMutex sharedLock = new InterProcessMutex(client, "/lock/curator_distri_lock");
        try {
            //锁是否被获取到
            //超时 不在进行操作
            boolean acquire = sharedLock.acquire(10, TimeUnit.MILLISECONDS);
            if (acquire) {
                System.out.println(name + "  is  get the shared lock");
                Thread.sleep(100000);
            }
        } catch (Exception e) {
            //日志记录一下 超时说明 有锁 可以不在操作
            System.out.println(name + "获取分布式锁异常");
        } finally {
            try {
                System.out.println(name + "  the flag is " + sharedLock.isAcquiredInThisProcess());
                //判断是否持有锁 进而进行锁是否释放的操作
                if (sharedLock.isAcquiredInThisProcess()) {
                    sharedLock.release();
                    System.out.println(name + "  is  release the shared lock");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void release() {
        if (client != null) {
            client.close();
        }
    }

    public void close() throws IOException {
        if (client != null) {
            client.close();
        }
    }

    public static void main(String[] args) {
        CuratorDistriLock curatorDistributeLock1 = new CuratorDistriLock("Thread1");
        CuratorDistriLock curatorDistributeLock2 = new CuratorDistriLock("Thread2");
        CuratorDistriLock curatorDistributeLock3 = new CuratorDistriLock("Thread3");

        curatorDistributeLock1.start();
        curatorDistributeLock2.start();
        curatorDistributeLock3.start();
    }
}
