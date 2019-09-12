package com.sunnysuperman.kvcache.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.sunnysuperman.commons.config.Config;
import com.sunnysuperman.commons.config.PropertiesConfig;
import com.sunnysuperman.commons.util.FileUtil;

public class TestConfig {
    private static PropertiesConfig config;
    static {
        config = new PropertiesConfig(TestConfig.class.getResourceAsStream("test.properties"));
    }

    public static Config get() {
        return config;
    }

    public static InputStream getResourceAsStream(String path) throws IOException {
        return TestConfig.class.getResourceAsStream(path);
    }

    public static File getResourceAsFile(String path) throws IOException {
        return new File(TestConfig.class.getResource(path).getFile());
    }

    public static String getResourceAsString(String path) throws IOException {
        return FileUtil.read(getResourceAsStream(path));
    }
}
