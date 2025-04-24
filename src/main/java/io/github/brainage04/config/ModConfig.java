package io.github.brainage04.config;

import java.util.*;

public class ModConfig {
    public final Set<String> blacklistedEnchantmentIds;

    public ModConfig() {
        this.blacklistedEnchantmentIds = new LinkedHashSet<>();
        this.blacklistedEnchantmentIds.addAll(List.of(
                "minecraft:binding_curse",
                "minecraft:vanishing_curse",
                "minecraft:blast_protection",
                "minecraft:projectile_protection",
                "minecraft:fire_protection",
                "minecraft:thorns",
                "minecraft:bane_of_arthropods",
                "minecraft:smite",
                "minecraft:knockback",
                "minecraft:frost_walker"
        ));
    }
}
