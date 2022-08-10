package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.kvcache.KvCacheException;

import redis.clients.jedis.util.SafeEncoder;

public class ShortConverter implements Converter<Short> {
	private static final ShortConverter INSTANCE = new ShortConverter();

	public static final ShortConverter getInstance() {
		return INSTANCE;
	}

	private ShortConverter() {

	}

	@Override
	public Short deserialize(byte[] value) throws KvCacheException {
		return Short.parseShort(SafeEncoder.encode(value));
	}

	@Override
	public byte[] serialize(Short model) throws KvCacheException {
		return SafeEncoder.encode(model.toString());
	}
}
