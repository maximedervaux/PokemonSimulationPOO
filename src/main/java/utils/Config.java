package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Properties;

public final class Config {
    private static final String FILE = "simulation.properties";

    private final Properties _properties = new Properties();

    private Config() { }

    public static Config load() throws IOException {
        InputStream in = Config.class.getClassLoader().getResourceAsStream(FILE);
        if (in == null) {
            throw new FileNotFoundException("Ressource introuvable : " + FILE);
        }

        Config config = new Config();
        try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            config._properties.load(reader);
        }
        return config;
    }

    public String getString(String key) {
        String value = System.getProperty(key, _properties.getProperty(key));
        if (value == null) {
            throw new NoSuchElementException("Paramètre absent de " + FILE + " : " + key);
        }
        return value;
    }

    public int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    public long getLong(String key) {
        return Long.parseLong(getString(key));
    }

    public double getDouble(String key) {
        return Double.parseDouble(getString(key));
    }
}
