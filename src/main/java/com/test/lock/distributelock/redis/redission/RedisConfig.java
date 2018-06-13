package com.test.lock.distributelock.redis.redission;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * @Author: liugang
 * @Date: 2018/6/13 10:54
 */
public class RedisConfig {

    private static RedissonClient client = null;

    static {
        Config config = new Config();
        //指定编码，默认编码为org.redisson.codec.JsonJacksonCodec
        //之前使用的spring-data-redis，用的客户端jedis，编码为org.springframework.data.redis.serializer.StringRedisSerializer
        //改用redisson后为了之间数据能兼容，这里修改编码为org.redisson.client.codec.StringCodec
        config.setCodec(new org.redisson.client.codec.StringCodec());
        //指定使用单节点部署方式
        config.useSingleServer().setAddress("redis://127.0.0.1:6379")
                //设置密码
                //config.setPassword("password")
                //设置对于master节点的连接池中连接数最大为500
                .setConnectionPoolSize(500)
                //如果当前连接池里的连接数量超过了最小空闲连接数，而同时有连接空闲时间超过了该数值，那么这些连接将会自动被关闭，并从连接池里去掉。时间单位是毫秒。
                .setIdleConnectionTimeout(10000)
                //同任何节点建立连接时的等待超时。时间单位是毫秒。
                .setConnectTimeout(30000)
                //等待节点回复命令的时间。该时间从命令发送成功时开始计时。
                .setTimeout(3000)
                //当与某个节点的连接断开时，等待与其重新建立连接的时间间隔。时间单位是毫秒。
                .setPingTimeout(30000)
                .setReconnectionTimeout(3000);

        //创建客户端(发现创建RedissonClient非常耗时，基本在2秒-4秒左右)
        client = Redisson.create(config);
    }


    public RedissonClient getRedis() {
        return client;
    }
}
