package com.test.lock.distributelock.zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.io.Closeable;
import java.io.IOException;

/**
 * @Author: liugang
 * @Date: 2018/6/13 16:43
 */
public class CuratorDistributeLock extends Thread implements Closeable {

    private CuratorFramework client;

    private String name;

    public CuratorDistributeLock(String name) {
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

            Stat stat = client.checkExists().forPath("/lock/curator_distribute_lock");
            if (null == stat) {
                String path = client.create().creatingParentsIfNeeded()
                        /**
                         * withMode节点类型
                         */
                        .withMode(CreateMode.PERSISTENT)
                        .forPath("/lock/curator_distribute_lock", "131".getBytes());
            }
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
        final LeaderLatch leaderLatch = new LeaderLatch(client, "/lock/curator_distribute_lock", "curator_distribute_lock:127.0.0.1:");
        leaderLatch.addListener(new LeaderLatchListener() {
            public void isLeader() {
                try {
                    System.out.println(name + "锁节点开始执行自己的事情。。。。。。。。。。do something");
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            public void notLeader() {
                System.out.println(name + "非锁节点。。。。。。。。。。do nothing");
            }
        });
        leaderLatch.start();
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
        CuratorDistributeLock curatorDistributeLock1 = new CuratorDistributeLock("Thread1");
        CuratorDistributeLock curatorDistributeLock2 = new CuratorDistributeLock("Thread2");
        CuratorDistributeLock curatorDistributeLock3 = new CuratorDistributeLock("Thread3");

        curatorDistributeLock1.start();
        curatorDistributeLock2.start();
        curatorDistributeLock3.start();
    }
}
