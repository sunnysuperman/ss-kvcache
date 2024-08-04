package com.sunnysuperman.kvcache;

import java.util.Collection;
import java.util.Map;

public interface SingleRepositoryProvider<T, K> extends RepositoryProvider<T, K> {

	@Override
	public default Map<K, T> findByKeys(Collection<K> keys) throws Exception {
		throw new UnsupportedOperationException("findByKeys");
	}

}
