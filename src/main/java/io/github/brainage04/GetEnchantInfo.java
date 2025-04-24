package io.github.brainage04;

import io.github.brainage04.commands.core.ModCommands;
import io.github.brainage04.config.ModConfig;
import io.github.brainage04.config.ModConfigManager;
import io.github.brainage04.event.ModWorldEvents;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
		ModWorldEvents.initialize();

		ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
			Set<RegistryEntry<Enchantment>> enchantments;
			if (stack.getItem() == Items.ENCHANTED_BOOK) {
				ItemEnchantmentsComponent storedEnchantments = stack.getComponents().get(DataComponentTypes.STORED_ENCHANTMENTS);
				if (storedEnchantments == null) return;

				enchantments = storedEnchantments.getEnchantments();
			} else {
				enchantments = EnchantmentHelper.getEnchantments(stack).getEnchantments();
			}

			for (int i = 0; i < lines.size(); i++) {
				Text line = lines.get(i);

				for (RegistryEntry<Enchantment> entry : enchantments) {
					Enchantment enchantment = entry.value();

					int level;
					if (stack.getItem() == Items.ENCHANTED_BOOK) {
						ItemEnchantmentsComponent storedEnchantments = stack.getComponents().get(DataComponentTypes.STORED_ENCHANTMENTS);
						if (storedEnchantments == null) continue;

						level = storedEnchantments.getLevel(entry);
					} else {
						level = EnchantmentHelper.getLevel(entry, stack);
					}

					if (line.getString().startsWith(Enchantment.getName(entry, level).getString()) && level == enchantment.getMaxLevel()) {
						lines.set(i, line.copy().formatted(Formatting.BOLD));
					}
				}
			}
		});

		MOD_CONFIG = ModConfigManager.load();

		LOGGER.info("{} initialized.", MOD_NAME);
	}
}