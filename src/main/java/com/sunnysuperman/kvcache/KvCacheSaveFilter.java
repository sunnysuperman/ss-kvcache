package com.sunnysuperman.kvcache;

public interface KvCacheSaveFilter<K, T> {

    boolean filter(K key, T value);

}
