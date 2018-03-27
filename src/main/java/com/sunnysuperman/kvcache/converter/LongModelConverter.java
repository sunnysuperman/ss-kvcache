package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.commons.util.ByteUtil;
import com.sunnysuperman.kvcache.KvCacheException;

public class LongModelConverter implements ModelConverter<Long> {
    private static final LongModelConverter INSTANCE = new LongModelConverter();

    public static final LongModelConverter getInstance() {
        return INSTANCE;
    }

    private LongModelConverter() {

    }

    @Override
    public Long deserialize(byte[] value) throws KvCacheException {
        return ByteUtil.toLong(value);
    }

    @Override
    public byte[] serialize(Long model) throws KvCacheException {
        return ByteUtil.fromLong(model);
    }
}
