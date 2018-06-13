package com.test.lock.distributelock.redis.springdata;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author: liugang
 * @Date: 2018/6/13 9:51
 */
public class RedisConfig {

    private static StringRedisTemplate redisStringTemplate;
    /**
     * redis 配置
     */
    private static JedisPool pool = null;

    static {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");
        redisStringTemplate = (StringRedisTemplate) ctx.getBean("redisTemplate");
    }

    public StringRedisTemplate getRedis() {
        return redisStringTemplate;
    }
}
