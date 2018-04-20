package com.globo.corp.srp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    public Properties load() throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("config.properties");
        Properties properties = new Properties();
        properties.load(in);
        return properties;
    }

}
