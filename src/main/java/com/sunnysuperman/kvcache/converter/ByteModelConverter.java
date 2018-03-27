package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.kvcache.KvCacheException;

public class ByteModelConverter implements ModelConverter<Byte> {
    private static final ByteModelConverter INSTANCE = new ByteModelConverter();

    public static final ByteModelConverter getInstance() {
        return INSTANCE;
    }

    private ByteModelConverter() {

    }

    @Override
    public Byte deserialize(byte[] value) throws KvCacheException {
        return value[0];
    }

    @Override
    public byte[] serialize(Byte model) throws KvCacheException {
        return new byte[] { model };
    }
}
