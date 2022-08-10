package com.sunnysuperman.kvcache;

public interface KvCacheSaveFilter<T, K> {

	boolean filter(K key, T value);

}
