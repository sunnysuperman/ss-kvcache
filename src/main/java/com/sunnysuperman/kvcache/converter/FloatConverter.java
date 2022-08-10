package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.kvcache.KvCacheException;

import redis.clients.jedis.util.SafeEncoder;

public class FloatConverter implements Converter<Float> {
	private static final FloatConverter INSTANCE = new FloatConverter();

	public static final FloatConverter getInstance() {
		return INSTANCE;
	}

	private FloatConverter() {

	}

	@Override
	public Float deserialize(byte[] value) throws KvCacheException {
		return Float.parseFloat(SafeEncoder.encode(value));
	}

	@Override
	public byte[] serialize(Float model) throws KvCacheException {
		return SafeEncoder.encode(model.toString());
	}
}
