package com.sunnysuperman.kvcache;

public interface KvCacheSaveFilter<T> {

    boolean shouldSave(T model);

}
