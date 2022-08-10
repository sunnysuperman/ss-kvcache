package com.sunnysuperman.kvcache.converter;

import com.sunnysuperman.commons.util.StringUtil;
import com.sunnysuperman.kvcache.KvCacheException;

public class StringConverter implements Converter<String> {
	private static final StringConverter INSTANCE = new StringConverter();

	public static final StringConverter getInstance() {
		return INSTANCE;
	}

	private StringConverter() {

	}

	@Override
	public String deserialize(byte[] value) throws KvCacheException {
		return new String(value, StringUtil.UTF8_CHARSET);
	}

	@Override
	public byte[] serialize(String model) throws KvCacheException {
		return model.getBytes(StringUtil.UTF8_CHARSET);
	}
}
