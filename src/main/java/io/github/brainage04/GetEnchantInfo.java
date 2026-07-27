package io.github.brainage04;

import io.github.brainage04.commands.core.ModCommands;
import io.github.brainage04.config.ModConfig;
import io.github.brainage04.config.ModConfigManager;
import io.github.brainage04.platform.ClientPlatform;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public final class GetEnchantInfo {
    public static final String MOD_ID = "getenchantinfo";
    public static final String MOD_NAME = "GetEnchantInfo";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ModConfig MOD_CONFIG;
    private GetEnchantInfo() {}
    public static void initialize(ClientPlatform platform, Path configDir) {
        LOGGER.info("{} initializing...", MOD_NAME);
        ModConfigManager.initialize(configDir);
        ModCommands.initialize(platform);
        platform.registerTooltipCallback(GetEnchantInfo::formatTooltip);
        MOD_CONFIG = ModConfigManager.load();
        LOGGER.info("{} initialized.", MOD_NAME);
    }
    private static void formatTooltip(ItemStack stack, List<Component> lines) {
        Set<Holder<Enchantment>> enchantments;
        if (stack.getItem() == Items.ENCHANTED_BOOK) {
            ItemEnchantments stored = stack.getComponents().get(DataComponents.STORED_ENCHANTMENTS);
            if (stored == null) return;
            enchantments = stored.keySet();
        } else enchantments = stack.getEnchantments().keySet();
        for (int i = 0; i < lines.size(); i++) for (Holder<Enchantment> entry : enchantments) {
            Enchantment enchantment = entry.value();
            int level = stack.getItem() == Items.ENCHANTED_BOOK ? stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).getLevel(entry) : EnchantmentHelper.getItemEnchantmentLevel(entry, stack);
            if (lines.get(i).getString().startsWith(Enchantment.getFullname(entry, level).getString()) && level == enchantment.getMaxLevel()) lines.set(i, lines.get(i).copy().withStyle(ChatFormatting.BOLD));
        }
    }
}
