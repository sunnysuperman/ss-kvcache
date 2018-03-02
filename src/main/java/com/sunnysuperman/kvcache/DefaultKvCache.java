package com.sunnysuperman.kvcache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.commons.util.ObjectUtil;

public class DefaultKvCache<K, T> implements KvCache<K, T> {
    protected static final Logger LOG = LoggerFactory.getLogger(DefaultKvCache.class);
    protected KvCacheExecutor executor;
    protected KvCachePolicy policy;
    protected RepositoryProvider<K, T> repository;
    protected ModelConverter<T> converter;
    protected KvCacheSaveFilter<T> saveFilter;

    public DefaultKvCache(KvCacheExecutor executor, KvCachePolicy policy, RepositoryProvider<K, T> repository,
            ModelConverter<T> converter, KvCacheSaveFilter<T> saveFilter) {
        this.executor = ObjectUtil.checkNotNull(executor);
        this.policy = ObjectUtil.checkNotNull(policy);
        policy.validate();
        this.repository = ObjectUtil.checkNotNull(repository);
        this.converter = ObjectUtil.checkNotNull(converter);
        this.saveFilter = saveFilter;
    }

    protected String makeKey(K id) {
        String prefix = policy.getPrefix();
        if (prefix == null || prefix.length() == 0) {
            return id.toString();
        }
        return prefix + id.toString();
    }

    private T findFromCache(K id) throws KvCacheException {
        String key = makeKey(id);
        String s = executor.find(key, policy);
        if (s == null) {
            return null;
        }
        return converter.deserialize(s);
    }

    @Override
    public void save(K id, T model) throws KvCacheException {
        String key = makeKey(id);
        String value = converter.serialize(model);
        executor.save(key, value, policy);
    }

    @Override
    public T findById(K id) throws KvCacheException {
        return findById(id, false);
    }

    @Override
    public T findById(K id, boolean cacheOnly) throws KvCacheException {
        if (id == null) {
            return null;
        }
        T model = null;
        // 从缓存中查找
        try {
            model = findFromCache(id);
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
            model = repository.findByIdFromRepository(id);
        } catch (Exception e) {
            throw new KvCacheException(e);
        }
        if (model == null) {
            return null;
        }
        // 保存到缓存
        if (saveFilter == null || saveFilter.shouldSave(model)) {
            try {
                save(id, model);
            } catch (Exception e) {
                LOG.error(null, e);
            }
        }
        return model;
    }

    @Override
    public Map<K, T> findByIds(Collection<K> ids) throws KvCacheException {
        if (ids == null || ids.isEmpty()) {
            return new HashMap<K, T>(0);
        }
        Map<K, T> cacheMap = new HashMap<K, T>(ids.size());
        boolean allCached = true;
        // 从缓存中查找，如果其中一个对象在缓存中找不到，则退出缓存查找
        try {
            for (K id : ids) {
                T model = findFromCache(id);
                if (model == null) {
                    allCached = false;
                    break;
                }
                cacheMap.put(id, model);
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
            freshMap = repository.findByIdsFromRepository(ids);
        } catch (Exception e) {
            throw new KvCacheException(e);
        }
        if (!freshMap.isEmpty()) {
            for (Entry<K, T> entry : freshMap.entrySet()) {
                K id = entry.getKey();
                if (!cacheMap.containsKey(id)) {
                    T model = entry.getValue();
                    if (saveFilter == null || saveFilter.shouldSave(model)) {
                        try {
                            save(id, model);
                        } catch (Exception e) {
                            LOG.error(null, e);
                        }
                    }
                }
            }
        }
        return freshMap;
    }

    @Override
    public void remove(K id) throws KvCacheException {
        String key = makeKey(id);
        executor.remove(key);
    }
}
