package com.sunnysuperman.kvcache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.commons.util.StringUtil;
import com.sunnysuperman.kvcache.converter.ModelConverter;

public class DefaultKvCache<K, T> implements KvCache<K, T> {
    protected static final Logger LOG = LoggerFactory.getLogger(DefaultKvCache.class);
    protected static final boolean INFO_ENABLED = LOG.isInfoEnabled();
    protected KvCacheExecutor executor;
    protected KvCachePolicy policy;
    protected RepositoryProvider<K, T> repository;
    protected ModelConverter<T> converter;
    protected KvCacheSaveFilter<K, T> saveFilter;

    public DefaultKvCache(KvCacheExecutor executor, KvCachePolicy policy, RepositoryProvider<K, T> repository,
            ModelConverter<T> converter, KvCacheSaveFilter<K, T> saveFilter) {
        this.executor = executor;
        this.policy = policy;
        this.repository = repository;
        this.converter = converter;
        this.saveFilter = saveFilter;
        policy.validate();
    }

    public KvCacheExecutor getExecutor() {
        return executor;
    }

    public KvCachePolicy getPolicy() {
        return policy;
    }

    public RepositoryProvider<K, T> getRepository() {
        return repository;
    }

    public ModelConverter<T> getConverter() {
        return converter;
    }

    public KvCacheSaveFilter<K, T> getSaveFilter() {
        return saveFilter;
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

    private Map<K, T> findFromCache(Collection<K> keys) throws KvCacheException {
        List<String> fullKeys = new ArrayList<>(keys.size());
        for (K key : keys) {
            fullKeys.add(makeFullKey(key));
        }
        Map<String, byte[]> bkv = executor.findMany(fullKeys, policy);
        List<String> foundKeys = INFO_ENABLED ? new ArrayList<String>(bkv.size()) : null;
        int i = -1;
        Map<K, T> kv = new HashMap<>();
        for (K key : keys) {
            i++;
            String fullKey = fullKeys.get(i);
            byte[] bvalue = bkv.get(fullKey);
            if (bvalue == null) {
                continue;
            }
            T value = converter.deserialize(bvalue);
            kv.put(key, value);
            if (foundKeys != null) {
                foundKeys.add(fullKey);
            }
        }
        if (INFO_ENABLED) {
            LOG.info("[KvCache] find <{}>, found: <{}>", StringUtil.join(fullKeys, " "),
                    StringUtil.join(foundKeys, " "));
        }
        return kv;
    }

    @Override
    public void save(K key, T value) throws KvCacheException {
        String fullKey = makeFullKey(key);
        byte[] data = converter.serialize(value);
        if (data == null) {
            throw new RuntimeException("[KvCache] could not serialize to null");
        }
        executor.save(fullKey, data, policy);
        if (INFO_ENABLED) {
            LOG.info("[KvCache] save <{}>", fullKey);
        }
    }

    @Override
    public void saveMany(Map<K, T> items) throws KvCacheException {
        if (items.isEmpty()) {
            return;
        }
        Map<String, byte[]> dataMap = new HashMap<>();
        for (Entry<K, T> entry : items.entrySet()) {
            String fullKey = makeFullKey(entry.getKey());
            byte[] data = converter.serialize(entry.getValue());
            if (data == null) {
                throw new RuntimeException("[KvCache] could not serialize to null");
            }
            dataMap.put(fullKey, data);
        }
        executor.saveMany(dataMap, policy);
        if (INFO_ENABLED) {
            LOG.info("[KvCache] saveMany <{}>", StringUtil.join(dataMap.keySet()));
        }
    }

    @Override
    public T refresh(K key) throws KvCacheException {
        return findAndSave(key, false);
    }

    private T findAndSave(K key, boolean ignoreSaveError) throws KvCacheException {
        // 从存储层中查找
        T model;
        try {
            model = repository.findByKey(key);
        } catch (Exception e) {
            throw new KvCacheException(e);
        }
        if (model == null) {
            return null;
        }
        // 保存到缓存
        if (saveFilter == null || saveFilter.filter(key, model)) {
            if (ignoreSaveError) {
                try {
                    save(key, model);
                } catch (Exception e) {
                    LOG.error(null, e);
                }
            } else {
                save(key, model);
            }
        }
        return model;
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
        return findAndSave(key, true);
    }

    @Override
    public Map<K, T> findByKeys(Collection<K> keys, boolean cacheOnly) throws KvCacheException {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<K, T> map = null;
        try {
            map = findFromCache(keys);
        } catch (Exception e) {
            LOG.error(null, e);
            map = Collections.emptyMap();
        }
        if (cacheOnly || map.size() == keys.size()) {
            return map;
        }
        List<K> queryKeys = new ArrayList<>(Math.max(keys.size() - map.size(), 1));
        for (K key : keys) {
            if (!map.containsKey(key)) {
                queryKeys.add(key);
            }
        }
        if (queryKeys.isEmpty()) {
            LOG.warn("queryKeys is empty, should not happened!!!");
            return map;
        }
        // 到repository中查找
        Map<K, T> freshMap;
        try {
            freshMap = repository.findByKeys(queryKeys);
        } catch (Exception e) {
            throw new KvCacheException(e);
        }
        if (freshMap.isEmpty()) {
            return map;
        }
        // 合并缓存结果和新查数据结果
        map.putAll(freshMap);
        // 保存新加入缓存的数据
        Map<K, T> saveItems;
        if (saveFilter == null) {
            saveItems = freshMap;
        } else {
            saveItems = new HashMap<>();
            for (Entry<K, T> entry : freshMap.entrySet()) {
                K key = entry.getKey();
                T value = entry.getValue();
                if (saveFilter.filter(key, value)) {
                    saveItems.put(key, value);
                }
            }
        }
        saveMany(saveItems);
        // 返回
        return map;
    }

    @Override
    public Map<K, T> findByKeys(Collection<K> keys) throws KvCacheException {
        return findByKeys(keys, false);
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
