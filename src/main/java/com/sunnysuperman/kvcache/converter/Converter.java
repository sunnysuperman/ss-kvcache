package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.kvcache.KvCacheException;

public interface Converter<T> {

	T deserialize(byte[] value) throws KvCacheException;

	byte[] serialize(T model) throws KvCacheException;

}
