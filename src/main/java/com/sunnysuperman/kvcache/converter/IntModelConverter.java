package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.kvcache.KvCacheException;

import redis.clients.jedis.util.SafeEncoder;

public class IntModelConverter implements ModelConverter<Integer> {
    private static final IntModelConverter INSTANCE = new IntModelConverter();

    public static final IntModelConverter getInstance() {
        return INSTANCE;
    }

    private IntModelConverter() {

    }

    @Override
    public Integer deserialize(byte[] value) throws KvCacheException {
        return Integer.parseInt(SafeEncoder.encode(value));
    }

    @Override
    public byte[] serialize(Integer model) throws KvCacheException {
        return SafeEncoder.encode(model.toString());
    }
}
