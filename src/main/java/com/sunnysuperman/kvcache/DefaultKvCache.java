package com.sunnysuperman.kvcache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.kvcache.converter.ModelConverter;

public class DefaultKvCache<K, T> implements KvCache<K, T> {
    protected static final Logger LOG = LoggerFactory.getLogger(DefaultKvCache.class);
    protected static final boolean INFO_ENABLED = LOG.isInfoEnabled();
    protected KvCacheExecutor executor;
    protected KvCachePolicy policy;
    protected RepositoryProvider<K, T> repository;
    protected ModelConverter<T> converter;
    protected KvCacheSaveFilter<T> saveFilter;

    public DefaultKvCache(KvCacheExecutor executor, KvCachePolicy policy, RepositoryProvider<K, T> repository,
            ModelConverter<T> converter, KvCacheSaveFilter<T> saveFilter) {
        this.executor = executor;
        this.policy = policy;
        this.repository = repository;
        this.converter = converter;
        this.saveFilter = saveFilter;
        policy.validate();
    }

    protected String makeFullKey(K key) {
        String prefix = policy.getPrefix();
        if (prefix == null || prefix.length() == 0) {
            return key.toString();
        }
        return prefix + key.toString();
    }

    private T findFromCache(K key) throws KvCacheException {
        String fullKey = makeFullKey(key);
        byte[] value = executor.find(fullKey, policy);
        if (INFO_ENABLED) {
            LOG.info("[KvCache] find <{}> <{}>", fullKey, value != null ? "cached" : "not found");
        }
        if (value == null) {
            return null;
        }
        return converter.deserialize(value);
    }

    @Override
    public void save(K key, T model) throws KvCacheException {
        String fullKey = makeFullKey(key);
        byte[] value = converter.serialize(model);
        if (value == null) {
            throw new RuntimeException("[KvCache] could not serialize to null");
        }
        executor.save(fullKey, value, policy);
        if (INFO_ENABLED) {
            LOG.info("[KvCache] save <{}>", fullKey);
        }
    }

    @Override
    public T findByKey(K key) throws KvCacheException {
        return findByKey(key, false);
    }

    @Override
    public T findByKey(K key, boolean cacheOnly) throws KvCacheException {
        if (key == null) {
            return null;
        }
        T model = null;
        // 从缓存中查找
        try {
            model = findFromCache(key);
        } catch (Exception e) {
            LOG.error(null, e);
        }
        if (model != null) {
            return model;
        }
        if (cacheOnly) {
            return null;
        }
        // 从db中查找
        try {
            model = repository.findByKey(key);
        } catch (Exception e) {
            throw new KvCacheException(e);
        }
        if (model == null) {
            return null;
        }
        // 保存到缓存
        if (saveFilter == null || saveFilter.shouldSave(model)) {
            try {
                save(key, model);
            } catch (Exception e) {
                LOG.error(null, e);
            }
        }
        return model;
    }

    @Override
    public Map<K, T> findByKeys(Collection<K> keys) throws KvCacheException {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<K, T> cacheMap = new HashMap<K, T>(keys.size());
        boolean allCached = true;
        // 从缓存中查找，如果其中一个对象在缓存中找不到，则退出缓存查找
        try {
            for (K key : keys) {
                T model = findFromCache(key);
                if (model == null) {
                    allCached = false;
                    break;
                }
                cacheMap.put(key, model);
            }
        } catch (Exception e) {
            LOG.error(null, e);
            allCached = false;
        }
        if (allCached) {
            return cacheMap;
        }
        // 到db中查找，然后再全部存入缓存
        Map<K, T> freshMap;
        try {
            freshMap = repository.findByKeys(keys);
        } catch (Exception e) {
            throw new KvCacheException(e);
        }
        if (freshMap.isEmpty()) {
            return freshMap;
        }
        for (Entry<K, T> entry : freshMap.entrySet()) {
            K key = entry.getKey();
            if (cacheMap.containsKey(key)) {
                continue;
            }
            T model = entry.getValue();
            if (saveFilter == null || saveFilter.shouldSave(model)) {
                try {
                    save(key, model);
                } catch (Exception e) {
                    LOG.error(null, e);
                }
            }
        }
        return freshMap;
    }

    @Override
    public void remove(K key) throws KvCacheException {
        String fullKey = makeFullKey(key);
        executor.remove(fullKey);
        if (INFO_ENABLED) {
            LOG.info("[KvCache] remove <{}>", fullKey);
        }
    }
}
