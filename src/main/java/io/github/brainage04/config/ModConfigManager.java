package io.github.brainage04.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.brainage04.GetEnchantInfo;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance()
                    .getConfigDir()
                    .resolve("%s.json".formatted(GetEnchantInfo.MOD_ID));

    public static ModConfig load() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            if (Files.notExists(CONFIG_PATH)) {
                ModConfig defaults = new ModConfig();
                String defaultJson = GSON.toJson(defaults);
                Files.writeString(CONFIG_PATH, defaultJson);
                return defaults;
            }

            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH);

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
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            GetEnchantInfo.LOGGER.error("Config not saved: ", e);
        }
    }
}
