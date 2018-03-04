package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.kvcache.KvCacheException;

public interface ModelConverter<T> {

    T deserialize(byte[] value) throws KvCacheException;

    byte[] serialize(T model) throws KvCacheException;

}
