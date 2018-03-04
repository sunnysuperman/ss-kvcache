package com.sunnysuperman.kvcache.converter;

import java.util.List;

import com.sunnysuperman.commons.bean.Bean;
import com.sunnysuperman.commons.util.JSONUtil;
import com.sunnysuperman.commons.util.StringUtil;
import com.sunnysuperman.kvcache.KvCacheException;

public class ListModelConverter<T> implements ModelConverter<List<T>> {
    private Class<T> clazz;

    public ListModelConverter(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public List<T> deserialize(byte[] value) throws KvCacheException {
        String s = new String(value, StringUtil.UTF8_CHARSET);
        return Bean.fromJson(s, clazz);
    }

    @Override
    public byte[] serialize(List<T> model) throws KvCacheException {
        String s = JSONUtil.toJSONString(model);
        return s.getBytes(StringUtil.UTF8_CHARSET);
    }

}
