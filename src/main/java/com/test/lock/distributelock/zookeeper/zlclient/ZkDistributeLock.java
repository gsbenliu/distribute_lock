package com.test.lock.distributelock.zookeeper.zlclient;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liugang
 * @Date: 2018/6/13 14:31
 */
public class ZkDistributeLock {
    /**
     * 1、创建一个永久性节点，作锁的根目录
     * 2、当要获取一个锁时，在锁目录下创建一个临时有序列的节点
     * 3、检查锁目录的子节点是否有序列比它小，若有则监听比它小的上一个节点，当前锁处于等待状态
     * 4、当等待时间超过Zookeeper session的连接时间（sessionTimeout）时，当前session过期，Zookeeper自动删除此session创建的临时节点，等待状态结束，获取锁失败
     * 5、当监听器触发时，等待状态结束，获得锁
     */

    ZookeeperConfig zookeeperConfig = new ZookeeperConfig();

    public String lock(String key, String data) throws Exception {
        String root = zookeeperConfig.getRoot();
        ZooKeeper zk = zookeeperConfig.getZk();
        String nodeid = zk.create(root + "/" + key, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        List<String> nodes = zk.getChildren(root, true);

        SortedSet<String> sortedNode = new TreeSet<String>();
        for (String node : nodes) {
            sortedNode.add(node);
        }

        if (nodeid.equals(sortedNode.first())) {
            return nodeid;
        }

        /**
         * 等待session是否超时
         */
        sortedNode.headSet(nodeid);



        return sortedNode.first();
    }

    public void release(String key) throws Exception {
        ZooKeeper zk = zookeeperConfig.getZk();
        zk.delete(key, -1);
        zk.close();
    }


    public static void main(String[] args) {
        String key = "my_zookeeper_lock";
        String value = "127.0.0.1:" + UUID.randomUUID().toString();
        ZkDistributeLock zkDistributeLock = new ZkDistributeLock();
        try {
            String nodeid = zkDistributeLock.lock(key, value);


            Thread.sleep(6000);

            zkDistributeLock.release(nodeid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
