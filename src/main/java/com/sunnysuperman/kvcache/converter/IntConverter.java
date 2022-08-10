package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.kvcache.KvCacheException;

import redis.clients.jedis.util.SafeEncoder;

public class IntConverter implements Converter<Integer> {
	private static final IntConverter INSTANCE = new IntConverter();

	public static final IntConverter getInstance() {
		return INSTANCE;
	}

	private IntConverter() {

	}

	@Override
	public Integer deserialize(byte[] value) throws KvCacheException {
		return Integer.parseInt(SafeEncoder.encode(value));
	}

	@Override
	public byte[] serialize(Integer model) throws KvCacheException {
		return SafeEncoder.encode(model.toString());
	}
}
