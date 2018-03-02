package com.sunnysuperman.kvcache;

public class KvCachePolicy {
    protected String prefix;
    protected int version;
    protected int expireIn;

    public void validate() {
        if (prefix == null) {
            throw new RuntimeException("Bad prefix");
        }
        if (version <= 0) {
            throw new RuntimeException("Bad version");
        }
        if (expireIn <= 0) {
            throw new RuntimeException("Bad expireIn");
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(int expireIn) {
        this.expireIn = expireIn;
    }

}
