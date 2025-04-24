package io.github.brainage04.commands;

import io.github.brainage04.GetEnchantInfo;
import io.github.brainage04.config.ModConfigManager;
import io.github.brainage04.util.EnchantmentUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class BlacklistedEnchantsCommand {
    private static final Set<Enchantment> blacklistedEnchants = new LinkedHashSet<>();

    public static Set<Enchantment> getBlacklistedEnchants() {
        return blacklistedEnchants;
    }

    public static int executeAdd(FabricClientCommandSource source, RegistryEntry<Enchantment> enchantmentRegistryEntry) {
        Enchantment enchantment = enchantmentRegistryEntry.value();

        if (blacklistedEnchants.contains(enchantment)) {
            source.sendError(EnchantmentUtils.getEnchantmentName(enchantmentRegistryEntry)
                    .append(" is already blacklisted!"));

            return 0;
        }

        blacklistedEnchants.add(enchantment);

        String enchantmentId = enchantmentRegistryEntry.getIdAsString();
        GetEnchantInfo.MOD_CONFIG.blacklistedEnchantmentIds.add(enchantmentId);

        source.sendFeedback(Text.empty()
                .append(EnchantmentUtils.getEnchantmentName(enchantmentRegistryEntry))
                .append(" is now blacklisted."));

        ModConfigManager.save();

        return 1;
    }

    public static int executeRemove(FabricClientCommandSource source, RegistryEntry<Enchantment> enchantmentRegistryEntry) {
        Enchantment enchantment = enchantmentRegistryEntry.value();

        if (!blacklistedEnchants.contains(enchantment)) {
            source.sendError(EnchantmentUtils.getEnchantmentName(enchantmentRegistryEntry)
                    .append(" is not blacklisted!"));

            return 0;
        }

        blacklistedEnchants.remove(enchantment);

        String enchantmentId = enchantmentRegistryEntry.getIdAsString();
        GetEnchantInfo.MOD_CONFIG.blacklistedEnchantmentIds.remove(enchantmentId);

        source.sendFeedback(Text.empty()
                .append(EnchantmentUtils.getEnchantmentName(enchantmentRegistryEntry))
                .append(" is no longer blacklisted."));

        ModConfigManager.save();

        return 1;
    }

    public static int executeQuery(FabricClientCommandSource source) {
        if (blacklistedEnchants.isEmpty()) {
            source.sendFeedback(Text.literal("No blacklisted enchantments."));

            return 1;
        }

        Optional<Registry<Enchantment>> optionalEnchantmentRegistry = source.getWorld().getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT);

        if (optionalEnchantmentRegistry.isEmpty()) {
            source.sendError(Text.literal("Enchantment registry could not be found!"));

            return 0;
        }

        Registry<Enchantment> enchantmentRegistry = optionalEnchantmentRegistry.get();

        source.sendFeedback(Text.literal("Enchantment blacklist:"));

        for (Enchantment enchantment : blacklistedEnchants) {
            source.sendFeedback(Text.literal(" - ")
                    .append(EnchantmentUtils.getEnchantmentName(enchantmentRegistry.getEntry(enchantment))));
        }

        return 1;
    }
}
