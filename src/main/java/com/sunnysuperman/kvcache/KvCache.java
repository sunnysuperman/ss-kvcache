package com.sunnysuperman.kvcache;

import java.util.Collection;
import java.util.Map;

public interface KvCache<K, T> {

    T findById(K id) throws KvCacheException;

    T findById(K id, boolean cacheOnly) throws KvCacheException;

    Map<K, T> findByIds(Collection<K> ids) throws KvCacheException;

    void save(K id, T model) throws KvCacheException;

    void remove(K id) throws KvCacheException;

}
