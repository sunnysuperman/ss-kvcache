package com.sunnysuperman.kvcache;

import java.util.Collection;
import java.util.Map;

public interface RepositoryProvider<K, T> {

    T findByIdFromRepository(K id) throws Exception;

    Map<K, T> findByIdsFromRepository(Collection<K> ids) throws Exception;

}
