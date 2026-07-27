package io.github.brainage04.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.brainage04.GetEnchantInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path configPath;

    public static void initialize(Path configDir) {
        configPath = configDir.resolve("%s.json".formatted(GetEnchantInfo.MOD_ID));
    }

    public static ModConfig load() {
        try {
            Files.createDirectories(configPath.getParent());

            if (Files.notExists(configPath)) {
                ModConfig defaults = new ModConfig();
                String defaultJson = GSON.toJson(defaults);
                Files.writeString(configPath, defaultJson);
                return defaults;
            }

            if (Files.exists(configPath)) {
                String json = Files.readString(configPath);

                return GSON.fromJson(json, ModConfig.class);
            }
        } catch (IOException e) {
            GetEnchantInfo.LOGGER.error("Config not loaded: ", e);
        }

        return new ModConfig();
    }

    public static void save() {
        try {
            String json = GSON.toJson(GetEnchantInfo.MOD_CONFIG);
            Files.writeString(configPath, json);
        } catch (IOException e) {
            GetEnchantInfo.LOGGER.error("Config not saved: ", e);
        }
    }
}
