package com.sunnysuperman.kvcache;

public interface ModelConverter<T> {

    T deserialize(String s) throws KvCacheException;

    String serialize(T model) throws KvCacheException;

}
