package com.sunnysuperman.kvcache;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface KvCacheExecutor {

    byte[] find(String key, KvCachePolicy policy);

    Map<String, byte[]> findMany(List<String> keys, KvCachePolicy policy);

    void save(String key, byte[] value, KvCachePolicy policy);

    void saveMany(Map<String, byte[]> items, KvCachePolicy policy);

    void remove(String key);

    void removeMany(Collection<String> keys);

    Long incrbyIfExists(String key, long num);

}
