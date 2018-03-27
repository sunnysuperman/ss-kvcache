package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.commons.bean.Bean;
import com.sunnysuperman.commons.bean.ParseBeanInterceptor;
import com.sunnysuperman.commons.bean.ParseBeanOptions;
import com.sunnysuperman.commons.util.JSONUtil;
import com.sunnysuperman.commons.util.StringUtil;
import com.sunnysuperman.kvcache.KvCacheException;

public class BeanModelConverter<T> implements ModelConverter<T> {
    private Class<T> modelClass;
    private ParseBeanInterceptor inteceptor;

    public BeanModelConverter(Class<T> modelClass) {
        this.modelClass = modelClass;
    }

    public BeanModelConverter(Class<T> modelClass, ParseBeanInterceptor inteceptor) {
        super();
        this.modelClass = modelClass;
        this.inteceptor = inteceptor;
    }

    @Override
    public T deserialize(byte[] value) throws KvCacheException {
        try {
            String s = new String(value, StringUtil.UTF8_CHARSET);
            if (inteceptor != null) {
                return Bean.fromJson(s, modelClass.newInstance(), new ParseBeanOptions().setInterceptor(inteceptor));
            } else {
                return Bean.fromJson(s, modelClass.newInstance());
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new KvCacheException(e);
        }
    }

    @Override
    public byte[] serialize(T model) throws KvCacheException {
        String s = JSONUtil.toJSONString(model);
        return s.getBytes(StringUtil.UTF8_CHARSET);
    }
}
