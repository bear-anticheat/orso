package com.bear.orso.common.config;

import com.moandjiezana.toml.Toml;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class OrsoConfig {

    @SneakyThrows
    private static Toml load0(Class<?> defResClass, Path dataDirectory, String name) {
        if (Files.notExists(dataDirectory)) {
            Files.createDirectory(dataDirectory);
        }

        Path configFile = dataDirectory.resolve(name + ".toml");
        if (Files.notExists(configFile)) {
            try (InputStream is = defResClass.getResourceAsStream("/defaults/" + name + ".toml")) {
                Objects.requireNonNull(is, "Resource not found: /defaults/" + name + ".toml in class " + defResClass.getName());
                Files.copy(is, configFile);
            }
        }

        Toml defaultConfig;
        try (InputStream is = defResClass.getResourceAsStream("/defaults/" + name + ".toml")) {
            Objects.requireNonNull(is, "Resource not found: /defaults/" + name + ".toml in class " + defResClass.getName());
            defaultConfig = new Toml().read(is);
        } catch (IllegalStateException ex) {
            throw new ConfigurationException("Error loading default configuration (" + name + ")", ex);
        }

        Toml config;
        try (InputStream is = Files.newInputStream(configFile)) {
            config = new Toml(defaultConfig).read(is);
        } catch (IllegalStateException ex) {
            throw new ConfigurationException("Error loading configuration (" + name + ")", ex);
        }

        return config;
    }

    public static Toml load(Class<?> defResClass, Path dataDirectory, String name) {
        return load0(defResClass, dataDirectory, name);
    }
}
