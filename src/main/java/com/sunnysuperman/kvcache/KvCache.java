package com.sunnysuperman.kvcache;

import java.util.Collection;
import java.util.Map;

public interface KvCache<K, T> {

    T findByKey(K key) throws KvCacheException;

    T findByKey(K key, boolean cacheOnly) throws KvCacheException;

    Map<K, T> findByKeys(Collection<K> keys) throws KvCacheException;

    // 保存到缓存中
    void save(K key, T model) throws KvCacheException;

    // 刷新缓存（重新加载内容，写入缓存并返回）
    T refresh(K key) throws KvCacheException;

    // 从缓存中删除
    void remove(K key) throws KvCacheException;

}
