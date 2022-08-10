package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.kvcache.KvCacheException;

import redis.clients.jedis.util.SafeEncoder;

public class DoubleConverter implements Converter<Double> {
	private static final DoubleConverter INSTANCE = new DoubleConverter();

	public static final DoubleConverter getInstance() {
		return INSTANCE;
	}

	private DoubleConverter() {

	}

	@Override
	public Double deserialize(byte[] value) throws KvCacheException {
		return Double.parseDouble(SafeEncoder.encode(value));
	}

	@Override
	public byte[] serialize(Double model) throws KvCacheException {
		return SafeEncoder.encode(model.toString());
	}
}
