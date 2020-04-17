package com.sunnysuperman.kvcache;

import java.util.Collection;
import java.util.Map;

public interface KvCache<K, T> {

    T findByKey(K key, boolean cacheOnly) throws KvCacheException;

    T findByKey(K key) throws KvCacheException;

    Map<K, T> findByKeys(Collection<K> keys, boolean cacheOnly) throws KvCacheException;

    Map<K, T> findByKeys(Collection<K> keys) throws KvCacheException;

    // 保存到缓存中
    void save(K key, T value) throws KvCacheException;

    void saveMany(Map<K, T> items) throws KvCacheException;

    // 刷新缓存（重新加载内容，写入缓存并返回）
    T refresh(K key) throws KvCacheException;

    // 从缓存中删除
    void remove(K key) throws KvCacheException;

    void removeMany(Collection<K> keys) throws KvCacheException;

    // 增加指定值(仅限指定缓存存在)
    Long incrbyIfExists(K key, long num) throws KvCacheException;

    // 增加指定浮点值(仅限指定缓存存在)
    Double incrbyIfExists(K key, double num) throws KvCacheException;

}
