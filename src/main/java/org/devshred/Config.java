package org.devshred;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private final Properties properties = new Properties();

    public static final Config INSTANCE = new Config();

    public Config() {
        final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProp(String key) {
        return properties.getProperty(key);
    }
}
