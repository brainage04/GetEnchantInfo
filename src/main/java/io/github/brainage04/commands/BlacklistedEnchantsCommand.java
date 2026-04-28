package io.github.brainage04.commands;

import io.github.brainage04.GetEnchantInfo;
import io.github.brainage04.config.ModConfigManager;
import io.github.brainage04.util.EnchantmentUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.LinkedHashSet;
import java.util.Set;

public class BlacklistedEnchantsCommand {
    private static final Set<Enchantment> blacklistedEnchants = new LinkedHashSet<>();

    public static Set<Enchantment> getBlacklistedEnchants() {
        return blacklistedEnchants;
    }

    public static int executeAdd(FabricClientCommandSource source, Holder<Enchantment> enchantmentHolder) {
        Enchantment enchantment = enchantmentHolder.value();

        if (blacklistedEnchants.contains(enchantment)) {
            source.sendError(EnchantmentUtils.getEnchantmentName(enchantmentHolder)
                    .append(" is already blacklisted!"));

            return 0;
        }

        blacklistedEnchants.add(enchantment);
        GetEnchantInfo.MOD_CONFIG.blacklistedEnchantmentIds.add(enchantmentId(enchantmentHolder));

        source.sendFeedback(Component.empty()
                .append(EnchantmentUtils.getEnchantmentName(enchantmentHolder))
                .append(" is now blacklisted."));

        ModConfigManager.save();

        return 1;
    }

    public static int executeRemove(FabricClientCommandSource source, Holder<Enchantment> enchantmentHolder) {
        Enchantment enchantment = enchantmentHolder.value();

        if (!blacklistedEnchants.contains(enchantment)) {
            source.sendError(EnchantmentUtils.getEnchantmentName(enchantmentHolder)
                    .append(" is not blacklisted!"));

            return 0;
        }

        blacklistedEnchants.remove(enchantment);
        GetEnchantInfo.MOD_CONFIG.blacklistedEnchantmentIds.remove(enchantmentId(enchantmentHolder));

        source.sendFeedback(Component.empty()
                .append(EnchantmentUtils.getEnchantmentName(enchantmentHolder))
                .append(" is no longer blacklisted."));

        ModConfigManager.save();

        return 1;
    }

    public static int executeQuery(FabricClientCommandSource source) {
        if (blacklistedEnchants.isEmpty()) {
            source.sendFeedback(Component.literal("No blacklisted enchantments."));

            return 1;
        }

        Registry<Enchantment> enchantmentRegistry = source.getClient().level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        source.sendFeedback(Component.literal("Enchantment blacklist:"));

        for (Enchantment enchantment : blacklistedEnchants) {
            source.sendFeedback(Component.literal(" - ")
                    .append(EnchantmentUtils.getEnchantmentName(enchantmentRegistry.wrapAsHolder(enchantment))));
        }

        return 1;
    }

    private static String enchantmentId(Holder<Enchantment> enchantmentHolder) {
        return enchantmentHolder.unwrapKey()
                .map(key -> key.identifier().toString())
                .orElseGet(enchantmentHolder::getRegisteredName);
    }
}
