package com.sunnysuperman.kvcache.redis;

import com.sunnysuperman.commons.util.StringUtil;
import com.sunnysuperman.kvcache.KvCacheExecutor;
import com.sunnysuperman.kvcache.KvCachePolicy;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.SafeEncoder;

public class RedisKvCacheExecutor implements KvCacheExecutor {
    private JedisPool pool;

    public RedisKvCacheExecutor(JedisPool pool) {
        super();
        this.pool = pool;
    }

    @Override
    public byte[] find(String key, KvCachePolicy policy) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.get(SafeEncoder.encode(key));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public void save(String key, byte[] value, KvCachePolicy policy) {
        Jedis jedis = null;
        int expireIn = policy.getExpireIn();
        try {
            jedis = pool.getResource();
            jedis.setex(key.getBytes(StringUtil.UTF8_CHARSET), expireIn, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public void remove(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.del(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

}
