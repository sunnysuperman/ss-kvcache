package com.sunnysuperman.kvcache;

public interface KvCacheExecutor {

    String find(String key, KvCachePolicy policy);

    void save(String key, String value, KvCachePolicy policy);

    void remove(String key);

}
