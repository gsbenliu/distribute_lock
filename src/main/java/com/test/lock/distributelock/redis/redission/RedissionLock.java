package com.test.lock.distributelock.redis.redission;

import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @Author: liugang
 * @Date: 2018/6/13 11:05
 */
public class RedissionLock {

    private static RedisConfig redisConfig = new RedisConfig();

    public boolean lock(String key) throws InterruptedException {
        RedissonClient redission = redisConfig.getRedis();
        RLock lock = redission.getLock(key);
        return lock.tryLock(10, TimeUnit.SECONDS);
    }

    public void release(String key) {
        RedissonClient redission = redisConfig.getRedis();
        RLock lock = redission.getLock(key);
        lock.unlock();
    }


    public static void main(String[] args) {
        RedissionLock redissionLock = new RedissionLock();
        String key = "my_redission_key";
        try {
            boolean f = redissionLock.lock(key);
            if (!f) {
                System.out.println("获取锁失败");
                return;
            }

            Thread.sleep(5000);

            redissionLock.release(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
