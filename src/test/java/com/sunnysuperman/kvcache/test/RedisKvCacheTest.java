package com.sunnysuperman.kvcache.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.sunnysuperman.kvcache.DefaultKvCache;
import com.sunnysuperman.kvcache.KvCachePolicy;
import com.sunnysuperman.kvcache.KvCacheSaveFilter;
import com.sunnysuperman.kvcache.RepositoryProvider;
import com.sunnysuperman.kvcache.converter.StringConverter;
import com.sunnysuperman.kvcache.redis.RedisKvCacheExecutor;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.util.SafeEncoder;

class RedisKvCacheTest {
	private static Properties cfg = new Properties();
	private static RedisKvCacheExecutor executor;

	@BeforeAll
	public static void setup() throws Exception {
		InputStream in = RedisKvCacheTest.class.getResourceAsStream("test.properties");
		cfg.load(in);

		JedisPoolConfig config = new JedisPoolConfig();
		config.setJmxEnabled(false);
		config.setMinIdle(1);
		config.setMaxIdle(1);
		config.setMaxTotal(1);
		JedisPool pool = new JedisPool(config, cfg.getProperty("redis.host"),
				Integer.parseInt(cfg.getProperty("redis.port")), Protocol.DEFAULT_TIMEOUT,
				cfg.getProperty("redis.password"), 0);
		executor = new RedisKvCacheExecutor(pool);
	}

	private void compareByteArray(byte[] b1, byte[] b2) {
		assertEquals(b1.length, b2.length);
		for (int i = 0; i < b1.length; i++) {
			assertEquals(b1[i], b2[i]);
		}
	}

	@Test
	void findMany1() {
		List<String> keys = new ArrayList<>();
		keys.add("1");
		keys.add("2");

		KvCachePolicy policy = new KvCachePolicy();
		policy.setExpireIn(3600);

		executor.save(keys.get(0), SafeEncoder.encode("abc"), policy);

		Map<String, byte[]> kv = executor.findMany(keys, policy);

		compareByteArray(kv.get(keys.get(0)), SafeEncoder.encode("abc"));
		assertNull(kv.get(keys.get(1)));
	}

	@Test
	void findMany2() {
		List<String> keys = new ArrayList<>();
		keys.add("1");
		keys.add("2");

		KvCachePolicy policy = new KvCachePolicy();
		policy.setExpireIn(3600);

		executor.save(keys.get(0), SafeEncoder.encode("abc"), policy);
		executor.save(keys.get(1), SafeEncoder.encode("def"), policy);

		Map<String, byte[]> kv = executor.findMany(Arrays.asList(keys.get(1), keys.get(0)), policy);

		compareByteArray(kv.get(keys.get(0)), SafeEncoder.encode("abc"));
		compareByteArray(kv.get(keys.get(1)), SafeEncoder.encode("def"));
	}

	@Test
	void findByKeys() {
		KvCachePolicy policy = new KvCachePolicy();
		policy.setExpireIn(3600);
		policy.setPrefix("");

		DefaultKvCache<String, Integer> cache = new DefaultKvCache<String, Integer>(executor, policy,
				new RepositoryProvider<String, Integer>() {

					@Override
					public String findByKey(Integer key) throws Exception {
						return "X" + key;
					}

					@Override
					public Map<Integer, String> findByKeys(Collection<Integer> keys) throws Exception {
						Map<Integer, String> map = new HashMap<Integer, String>();
						for (Integer key : keys) {
							map.put(key, "X" + key);
						}
						return map;
					}

				}, StringConverter.getInstance(), null);

		List<Integer> keys = Arrays.asList(1, 3, 5);
		for (Integer key : keys) {
			cache.getExecutor().remove(String.valueOf(key));
			assertTrue(cache.findByKey(key, true) == null);
			assertTrue(cache.findByKey(key) != null);
			assertTrue(cache.findByKey(key, true) != null);
		}
		assertTrue(cache.findByKeys(keys).size() == 3);

		List<Integer> anotherKeys = Arrays.asList(7, 9);
		for (Integer key : anotherKeys) {
			cache.getExecutor().remove(key.toString());
		}
		List<Integer> keys2 = new ArrayList<>(keys);
		keys2.addAll(anotherKeys);

		assertTrue(cache.findByKeys(keys2, true).size() == keys.size());
		assertTrue(cache.findByKeys(keys2).size() == keys2.size());
		assertTrue(cache.findByKeys(keys2).size() == keys2.size());
	}

	@Test
	void saveFilter() {
		KvCachePolicy policy = new KvCachePolicy();
		policy.setExpireIn(3600);
		policy.setPrefix("");

		DefaultKvCache<String, Integer> cache = new DefaultKvCache<String, Integer>(executor, policy,
				new RepositoryProvider<String, Integer>() {

					@Override
					public String findByKey(Integer key) throws Exception {
						return "X" + key;
					}

					@Override
					public Map<Integer, String> findByKeys(Collection<Integer> keys) throws Exception {
						Map<Integer, String> map = new HashMap<Integer, String>();
						for (Integer key : keys) {
							map.put(key, "X" + key);
						}
						return map;
					}

				}, StringConverter.getInstance(), new KvCacheSaveFilter<String, Integer>() {

					@Override
					public boolean filter(Integer key, String value) {
						return key < 10;
					}

				});

		List<Integer> keys = Arrays.asList(1, 9);
		for (Integer key : keys) {
			cache.getExecutor().remove(String.valueOf(key));
			assertTrue(cache.findByKey(key, true) == null);
			assertTrue(cache.findByKey(key) != null);
			assertTrue(cache.findByKey(key, true) != null);
		}

		List<Integer> keys2 = Arrays.asList(10, 11);
		for (Integer key : keys2) {
			cache.getExecutor().remove(String.valueOf(key));
			assertTrue(cache.findByKey(key, true) == null);
			assertTrue(cache.findByKey(key) != null);
			assertTrue(cache.findByKey(key, true) == null);
		}
	}
}
