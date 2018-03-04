package com.sunnysuperman.kvcache;

public interface KvCacheExecutor {

    byte[] find(String key, KvCachePolicy policy);

    void save(String key, byte[] value, KvCachePolicy policy);

    void remove(String key);

}
