package com.sunnysuperman.kvcache;

public class KvCacheException extends RuntimeException {
    private static final long serialVersionUID = 8595638497781974760L;

    public KvCacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public KvCacheException(String message) {
        super(message);
    }

    public KvCacheException(Throwable cause) {
        super(cause);
    }

}
