package com.test.lock.distributelock.redis.Jedis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author: liugang
 * @Date: 2018/6/13 9:51
 */
public class RedisConfig {

    /**
     * redis 配置
     */
    private static JedisPool pool = null;

    static {
        JedisPoolConfig config = new JedisPoolConfig();
        // 设置最大连接数
        config.setMaxTotal(200);
        // 设置最大空闲数
        config.setMaxIdle(8);
        // 设置最大等待时间
        config.setMaxWaitMillis(1000 * 100);
        // 在borrow一个jedis实例时，是否需要验证，若为true，则所有jedis实例均是可用的
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
        config.setTestOnCreate(false);
        config.setTestWhileIdle(false);
        pool = new JedisPool(config, "127.0.0.1", 6379, 3000);
    }

    public JedisPool getRedis() {
        return pool;
    }
}
