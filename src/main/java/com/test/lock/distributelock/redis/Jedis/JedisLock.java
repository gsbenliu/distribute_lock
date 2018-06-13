package com.test.lock.distributelock.redis.Jedis;

import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.UUID;

/**
 * @Author: liugang
 * @Date: 2018/6/13 9:46
 */
public class JedisLock {

    private static RedisConfig redisConfig = new RedisConfig();

    public String lock(String key, String val, int timeOut) {
        Jedis jedis = redisConfig.getRedis().getResource();
        String result = jedis.set(key, val, "NX", "PX", timeOut);
        return result;
    }

    public boolean release(String key, String value) {
        Jedis jedis = redisConfig.getRedis().getResource();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(value));
        if (Integer.parseInt(result.toString()) == 1) {
            return true;
        }
        return false;
    }


    public static void main(String[] args) {
        String key = "my_redis_lock";
        String value = "127.0.0.1:" + UUID.randomUUID().toString();
        JedisLock jedisLock = new JedisLock();
        try {
            System.out.println(jedisLock.lock(key, value, 6000));
            Thread.sleep(4000);

            System.out.println(jedisLock.release(key, value));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
