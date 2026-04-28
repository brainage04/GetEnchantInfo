package io.github.brainage04;

import io.github.brainage04.commands.core.ModCommands;
import io.github.brainage04.config.ModConfig;
import io.github.brainage04.config.ModConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class GetEnchantInfo implements ClientModInitializer {
	public static final String MOD_ID = "getenchantinfo";
	public static final String MOD_NAME = "GetEnchantInfo";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ModConfig MOD_CONFIG;

	@Override
	public void onInitializeClient() {
		LOGGER.info("{} initializing...", MOD_NAME);

		ModCommands.initialize();

		ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
			Set<Holder<Enchantment>> enchantments;
			if (stack.getItem() == Items.ENCHANTED_BOOK) {
				ItemEnchantments storedEnchantments = stack.getComponents().get(DataComponents.STORED_ENCHANTMENTS);
				if (storedEnchantments == null) return;

				enchantments = storedEnchantments.keySet();
			} else {
				enchantments = stack.getEnchantments().keySet();
			}

			for (int i = 0; i < lines.size(); i++) {
				Component line = lines.get(i);

				for (Holder<Enchantment> entry : enchantments) {
					Enchantment enchantment = entry.value();
					int level = stack.getItem() == Items.ENCHANTED_BOOK
							? stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).getLevel(entry)
							: EnchantmentHelper.getItemEnchantmentLevel(entry, stack);

					if (line.getString().startsWith(Enchantment.getFullname(entry, level).getString())
							&& level == enchantment.getMaxLevel()) {
						lines.set(i, line.copy().withStyle(ChatFormatting.BOLD));
					}
				}
			}
		});

		MOD_CONFIG = ModConfigManager.load();

		LOGGER.info("{} initialized.", MOD_NAME);
	}
}
