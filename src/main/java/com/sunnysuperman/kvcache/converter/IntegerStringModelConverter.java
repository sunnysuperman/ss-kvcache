package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.kvcache.KvCacheException;

import redis.clients.util.SafeEncoder;

public class IntegerStringModelConverter implements ModelConverter<Integer> {
    private static final IntegerStringModelConverter INSTANCE = new IntegerStringModelConverter();

    public static final IntegerStringModelConverter getInstance() {
        return INSTANCE;
    }

    private IntegerStringModelConverter() {

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
