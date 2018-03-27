package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.commons.util.ByteUtil;
import com.sunnysuperman.kvcache.KvCacheException;

public class IntegerModelConverter implements ModelConverter<Integer> {
    private static final IntegerModelConverter INSTANCE = new IntegerModelConverter();

    public static final IntegerModelConverter getInstance() {
        return INSTANCE;
    }

    private IntegerModelConverter() {

    }

    @Override
    public Integer deserialize(byte[] value) throws KvCacheException {
        return ByteUtil.toInt(value);
    }

    @Override
    public byte[] serialize(Integer model) throws KvCacheException {
        return ByteUtil.fromInt(model);
    }
}
