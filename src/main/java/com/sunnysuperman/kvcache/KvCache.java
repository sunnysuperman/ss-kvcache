package com.sunnysuperman.kvcache;

import java.util.Collection;
import java.util.Map;

public interface KvCache<K, T> {

    T findByKey(K key) throws KvCacheException;

    T findByKey(K key, boolean cacheOnly) throws KvCacheException;

    Map<K, T> findByKeys(Collection<K> keys) throws KvCacheException;

    void save(K key, T model) throws KvCacheException;

    void remove(K key) throws KvCacheException;

}
