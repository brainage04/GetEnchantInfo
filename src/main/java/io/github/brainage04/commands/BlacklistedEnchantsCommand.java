package io.github.brainage04.commands;

import net.minecraft.client.Minecraft;

import io.github.brainage04.GetEnchantInfo;
import io.github.brainage04.config.ModConfigManager;
import io.github.brainage04.util.EnchantmentUtils;
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

    public static int executeAdd(net.minecraft.commands.SharedSuggestionProvider source, Holder<Enchantment> enchantmentHolder) {
        Enchantment enchantment = enchantmentHolder.value();

        if (blacklistedEnchants.contains(enchantment)) {
            feedback(EnchantmentUtils.getEnchantmentName(enchantmentHolder)
                    .append(" is already blacklisted!"));

            return 0;
        }

        blacklistedEnchants.add(enchantment);
        GetEnchantInfo.MOD_CONFIG.blacklistedEnchantmentIds.add(enchantmentId(enchantmentHolder));

        feedback(Component.empty()
                .append(EnchantmentUtils.getEnchantmentName(enchantmentHolder))
                .append(" is now blacklisted."));

        ModConfigManager.save();

        return 1;
    }

    public static int executeRemove(net.minecraft.commands.SharedSuggestionProvider source, Holder<Enchantment> enchantmentHolder) {
        Enchantment enchantment = enchantmentHolder.value();

        if (!blacklistedEnchants.contains(enchantment)) {
            feedback(EnchantmentUtils.getEnchantmentName(enchantmentHolder)
                    .append(" is not blacklisted!"));

            return 0;
        }

        blacklistedEnchants.remove(enchantment);
        GetEnchantInfo.MOD_CONFIG.blacklistedEnchantmentIds.remove(enchantmentId(enchantmentHolder));

        feedback(Component.empty()
                .append(EnchantmentUtils.getEnchantmentName(enchantmentHolder))
                .append(" is no longer blacklisted."));

        ModConfigManager.save();

        return 1;
    }

    public static int executeQuery(net.minecraft.commands.SharedSuggestionProvider source) {
        if (blacklistedEnchants.isEmpty()) {
            feedback(Component.literal("No blacklisted enchantments."));

            return 1;
        }

        Registry<Enchantment> enchantmentRegistry = Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        feedback(Component.literal("Enchantment blacklist:"));

        for (Enchantment enchantment : blacklistedEnchants) {
            feedback(Component.literal(" - ")
                    .append(EnchantmentUtils.getEnchantmentName(enchantmentRegistry.wrapAsHolder(enchantment))));
        }

        return 1;
    }

    private static String enchantmentId(Holder<Enchantment> enchantmentHolder) {
        return enchantmentHolder.unwrapKey()
                .map(key -> key.identifier().toString())
                .orElseGet(enchantmentHolder::getRegisteredName);
    }
    private static void feedback(net.minecraft.network.chat.Component message) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) minecraft.player.sendSystemMessage(message);
    }

}
