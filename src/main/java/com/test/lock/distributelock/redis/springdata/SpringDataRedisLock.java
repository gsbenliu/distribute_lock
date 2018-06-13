package com.test.lock.distributelock.redis.springdata;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.UUID;

/**
 * @Author: liugang
 * @Date: 2018/6/13 13:04
 */
public class SpringDataRedisLock {


    private static RedisConfig redisConfig = new RedisConfig();

    public boolean lock(final String key, final String value) throws InterruptedException {
        final StringRedisTemplate redisTemplate = redisConfig.getRedis();
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                boolean lock = redisConnection.setNX(serializer.serialize(key), serializer.serialize(value));
                if (lock) {
                    /**
                     * 此处容易造成死锁
                     * 一旦我设置过期时间失效，那么key没有失效时间。在无法释放锁的情况下。分布式锁一直存在。造成死锁
                     *
                     */
                    redisConnection.expire(serializer.serialize(key), 60000);
                }
                return lock;
            }
        });
    }

    public Long release(final String key) {
        final StringRedisTemplate redisTemplate = redisConfig.getRedis();
        return redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                return redisConnection.del(serializer.serialize(key));
            }
        });
    }


    public static void main(String[] args) {
        SpringDataRedisLock springDataRedisLock = new SpringDataRedisLock();
        String key = "my_redis_lock";
        String value = "127.0.0.1:" + UUID.randomUUID().toString();
        try {
            boolean lock = springDataRedisLock.lock(key, value);
            System.out.println(lock);
            if (!lock) {
                System.out.println("分布式锁获取失败");
                return;
            }
            Thread.sleep(6000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println(springDataRedisLock.release(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
