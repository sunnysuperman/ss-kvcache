package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.kvcache.KvCacheException;

import redis.clients.jedis.util.SafeEncoder;

public class LongConverter implements Converter<Long> {
	private static final LongConverter INSTANCE = new LongConverter();

	public static final LongConverter getInstance() {
		return INSTANCE;
	}

	private LongConverter() {

	}

	@Override
	public Long deserialize(byte[] value) throws KvCacheException {
		return Long.parseLong(SafeEncoder.encode(value));
	}

	@Override
	public byte[] serialize(Long model) throws KvCacheException {
		return SafeEncoder.encode(model.toString());
	}
}
