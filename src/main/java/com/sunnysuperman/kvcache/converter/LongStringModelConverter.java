package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.kvcache.KvCacheException;

import redis.clients.util.SafeEncoder;

public class LongStringModelConverter implements ModelConverter<Long> {
    private static final LongStringModelConverter INSTANCE = new LongStringModelConverter();

    public static final LongStringModelConverter getInstance() {
        return INSTANCE;
    }

    private LongStringModelConverter() {

    }

    @Override
    public Long deserialize(byte[] value) throws KvCacheException {
        return Long.parseLong(SafeEncoder.encode(value));
    }

    @Override
    public byte[] serialize(Long model) throws KvCacheException {
        return SafeEncoder.encode(model.toString());
    }
}
