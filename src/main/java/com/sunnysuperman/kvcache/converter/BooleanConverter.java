package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.kvcache.KvCacheException;

public class BooleanConverter implements Converter<Boolean> {
	private static final BooleanConverter INSTANCE = new BooleanConverter();

	public static final BooleanConverter getInstance() {
		return INSTANCE;
	}

	private BooleanConverter() {

	}

	@Override
	public Boolean deserialize(byte[] value) throws KvCacheException {
		return value[0] > 0;
	}

	@Override
	public byte[] serialize(Boolean model) throws KvCacheException {
		byte b = (byte) (model.booleanValue() ? 1 : 0);
		return new byte[] { b };
	}

}
