package com.test.lock.distributelock.zookeeper.zlclient;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

/**
 * @Author: liugang
 * @Date: 2018/6/13 14:25
 */
public class ZookeeperConfig {
    /**
     * 连接串
     */
    private static String connectionString = "localhost:2181";
    private static String root = "/zkDistributeLock";

    public String getRoot() {
        return root;
    }

    /**
     * 回话超时时间
     */
    private static int sessionTimeout = 10000;

    private static ZooKeeper zk = null;

    static {
        try {
            zk = new ZooKeeper(connectionString, sessionTimeout, null);
            Stat s = zk.exists(root, false);
            if (s == null) {
                zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ZooKeeper getZk() {
        return zk;
    }
}
