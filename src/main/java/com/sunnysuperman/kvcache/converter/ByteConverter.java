package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.kvcache.KvCacheException;

public class ByteConverter implements Converter<Byte> {
	private static final ByteConverter INSTANCE = new ByteConverter();

	public static final ByteConverter getInstance() {
		return INSTANCE;
	}

	private ByteConverter() {

	}

	@Override
	public Byte deserialize(byte[] value) throws KvCacheException {
		return value[0];
	}

	@Override
	public byte[] serialize(Byte model) throws KvCacheException {
		return new byte[] { model };
	}
}
