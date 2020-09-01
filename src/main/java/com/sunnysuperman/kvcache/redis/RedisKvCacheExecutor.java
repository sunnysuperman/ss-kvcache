package com.sunnysuperman.kvcache.redis;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sunnysuperman.commons.util.FormatUtil;
import com.sunnysuperman.kvcache.KvCacheExecutor;
import com.sunnysuperman.kvcache.KvCachePolicy;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.util.SafeEncoder;

public class RedisKvCacheExecutor implements KvCacheExecutor {
    private static final String INCREASE_IF_EXISTS = "local v=redis.call('exists', KEYS[1]);if(v==0) then return nil;else return redis.call('incrby', KEYS[1], ARGV[1]);end";
    private static final String INCREASE_FLOAT_IF_EXISTS = "local v=redis.call('exists', KEYS[1]);if(v==0) then return nil;else return redis.call('incrbyfloat', KEYS[1], ARGV[1]);end";

    protected JedisPool pool;

    public RedisKvCacheExecutor(JedisPool pool) {
        super();
        this.pool = pool;
    }

    public JedisPool getPool() {
        return pool;
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
    public Map<String, byte[]> findMany(List<String> keys, KvCachePolicy policy) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            byte[][] bkeys = new byte[keys.size()][0];
            int i = 0;
            for (String key : keys) {
                bkeys[i] = SafeEncoder.encode(key);
                i++;
            }
            List<byte[]> values = jedis.mget(bkeys);
            i = -1;
            Map<String, byte[]> kv = new HashMap<>();
            for (String key : keys) {
                i++;
                byte[] value = values.get(i);
                if (value == null) {
                    continue;
                }
                kv.put(key, value);
            }
            return kv;
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
            jedis.setex(SafeEncoder.encode(key), expireIn, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public void saveMany(Map<String, byte[]> items, KvCachePolicy policy) {
        Jedis jedis = null;
        int expireIn = policy.getExpireIn();
        try {
            jedis = pool.getResource();
            for (Entry<String, byte[]> item : items.entrySet()) {
                jedis.setex(SafeEncoder.encode(item.getKey()), expireIn, item.getValue());
            }
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

    @Override
    public void removeMany(Collection<String> keys) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.del(keys.toArray(new String[keys.size()]));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Long incrbyIfExists(String key, long num) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            Object result = jedis.eval(INCREASE_IF_EXISTS, Arrays.asList(key),
                    Collections.singletonList(String.valueOf(num)));
            return FormatUtil.parseLong(result);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Double incrbyIfExists(String key, double num) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            Object result = jedis.eval(INCREASE_FLOAT_IF_EXISTS, Arrays.asList(key),
                    Collections.singletonList(String.valueOf(num)));
            return FormatUtil.parseDouble(result);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

}
