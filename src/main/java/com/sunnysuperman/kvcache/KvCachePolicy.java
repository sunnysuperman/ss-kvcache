package com.sunnysuperman.kvcache;

public class KvCachePolicy {
	protected String prefix;
	protected int expireIn;

	public void validate() {
		if (prefix == null) {
			throw new RuntimeException("Bad prefix");
		}
		if (expireIn <= 0) {
			throw new RuntimeException("Bad expireIn");
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public KvCachePolicy setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public int getExpireIn() {
		return expireIn;
	}

	public KvCachePolicy setExpireIn(int expireIn) {
		this.expireIn = expireIn;
		return this;
	}

}
