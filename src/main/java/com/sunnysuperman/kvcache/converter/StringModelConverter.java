package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.commons.util.StringUtil;
import com.sunnysuperman.kvcache.KvCacheException;

public class StringModelConverter implements ModelConverter<String> {
    private static final StringModelConverter INSTANCE = new StringModelConverter();

    public static final StringModelConverter getInstance() {
        return INSTANCE;
    }

    private StringModelConverter() {

    }

    @Override
    public String deserialize(byte[] value) throws KvCacheException {
        return new String(value, StringUtil.UTF8_CHARSET);
    }

    @Override
    public byte[] serialize(String model) throws KvCacheException {
        return model.getBytes(StringUtil.UTF8_CHARSET);
    }
}
