package com.sunnysuperman.kvcache.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.commons.util.StringUtil;
import com.sunnysuperman.kvcache.KvCacheExecutor;
import com.sunnysuperman.kvcache.KvCachePolicy;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisKvCacheExecutor implements KvCacheExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(RedisKvCacheExecutor.class);
    private static final boolean VERBOSE_ENABLED = LOG.isInfoEnabled();
    private JedisPool pool;

    public RedisKvCacheExecutor(JedisPool pool) {
        super();
        this.pool = pool;
    }

    private void verbose(String s) {
        LOG.info("[DefaultKvCache] " + s);
    }

    private void error(String s) {
        LOG.info("[DefaultKvCache] " + s);
    }

    @Override
    public String find(String key, KvCachePolicy policy) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = pool.getResource();
            value = jedis.get(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            if (VERBOSE_ENABLED) {
                verbose("find " + key + ", " + value);
            }
        }
        if (value == null) {
            return null;
        }
        int offset = value.indexOf('/');
        if (offset < 0) {
            error("Bad value: " + value + " of " + key);
            return null;
        }
        int version = Integer.parseInt(value.substring(0, offset));
        if (version != policy.getVersion()) {
            return null;
        }
        value = value.substring(offset + 1);
        return value;
    }

    @Override
    public void save(String key, String value, KvCachePolicy policy) {
        byte[] b1 = (policy.getVersion() + "/").getBytes(StringUtil.UTF8_CHARSET);
        byte[] b2 = value.getBytes(StringUtil.UTF8_CHARSET);
        byte[] bytes = new byte[b1.length + b2.length];
        System.arraycopy(b1, 0, bytes, 0, b1.length);
        System.arraycopy(b2, 0, bytes, b1.length, b2.length);
        Jedis jedis = null;
        int expireIn = policy.getExpireIn();
        try {
            jedis = pool.getResource();
            jedis.setex(key.getBytes(StringUtil.UTF8_CHARSET), expireIn, bytes);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            if (VERBOSE_ENABLED) {
                verbose("save " + key + ": " + value + " ,ex: " + expireIn);
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
            if (VERBOSE_ENABLED) {
                verbose("remove " + key);
            }
        }
    }

}
