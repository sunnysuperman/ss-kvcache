package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.commons.util.ByteUtil;
import com.sunnysuperman.kvcache.KvCacheException;

public class IntegerModelConverter implements ModelConverter<Integer> {

    @Override
    public Integer deserialize(byte[] value) throws KvCacheException {
        return ByteUtil.bytes2int(value);
    }

    @Override
    public byte[] serialize(Integer model) throws KvCacheException {
        return ByteUtil.int2bytes(model);
    }

}
