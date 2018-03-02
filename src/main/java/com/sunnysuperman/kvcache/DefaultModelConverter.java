package com.sunnysuperman.kvcache;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunnysuperman.commons.bean.Bean;
import com.sunnysuperman.commons.util.JSONUtil;

public class DefaultModelConverter<T> implements ModelConverter<T> {
    private Class<T> modelClass;

    public DefaultModelConverter(Class<T> modelClass) {
        this.modelClass = modelClass;
    }

    @Override
    public T deserialize(String s) throws KvCacheException {
        try {
            return Bean.fromJson(s, modelClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new KvCacheException(e);
        }
    }

    @Override
    public String serialize(T model) throws KvCacheException {
        return JSONUtil.toJSONString(model, null, SerializerFeature.DisableCircularReferenceDetect);
    }
}
