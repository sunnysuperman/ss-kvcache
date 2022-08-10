package com.sunnysuperman.kvcache;

import java.util.Collection;
import java.util.Map;

public interface RepositoryProvider<T, K> {

	T findByKey(K key) throws Exception;

	Map<K, T> findByKeys(Collection<K> keys) throws Exception;

}
