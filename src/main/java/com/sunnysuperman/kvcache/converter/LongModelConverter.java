package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.commons.util.ByteUtil;
import com.sunnysuperman.kvcache.KvCacheException;

public class LongModelConverter implements ModelConverter<Long> {

    @Override
    public Long deserialize(byte[] value) throws KvCacheException {
        return ByteUtil.bytes2long(value);
    }

    @Override
    public byte[] serialize(Long model) throws KvCacheException {
        return ByteUtil.long2bytes(model);
    }

}
