package io.github.brainage04.commands;

import io.github.brainage04.util.EnchantmentUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GetEnchantInfoCommand {
    public static void sendEnchantmentInfo(
            FabricClientCommandSource source,
            Registry<Enchantment> enchantmentRegistry,
            Enchantment enchantment
    ) {
        Identifier enchantmentId = enchantmentRegistry.getKey(enchantment);
        if (enchantmentId == null) return;

        Holder<Enchantment> enchantmentHolder = enchantmentRegistry.wrapAsHolder(enchantment);

        source.sendFeedback(Component.literal("Enchant info for ")
                .append(EnchantmentUtils.getEnchantmentName(enchantmentHolder))
                .append(":")
                .withStyle(ChatFormatting.BOLD));

        source.sendFeedback(Component.literal("ID: %s".formatted(enchantmentId)));
        source.sendFeedback(Component.literal("Max level: %d".formatted(enchantment.getMaxLevel())));
        source.sendFeedback(Component.literal("Incompatible with: ")
                .append(joinIncompatibleEnchantmentNames(enchantmentRegistry, enchantment)));
        source.sendFeedback(Component.literal("Applied to: ")
                .append(joinItemNames(enchantment.getSupportedItems().stream().map(Holder::value).toList())));
    }

    public static int execute(FabricClientCommandSource source, String desiredEnchantmentString) {
        Registry<Enchantment> enchantmentRegistry = source.getClient().level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        Enchantment exactMatch = null;
        List<Enchantment> potentialMatches = new ArrayList<>();
        for (Enchantment enchantment : enchantmentRegistry) {
            String enchantmentString = EnchantmentUtils.getEnchantmentName(enchantmentRegistry.wrapAsHolder(enchantment))
                    .getString()
                    .toLowerCase();

            if (enchantmentString.equals(desiredEnchantmentString)) {
                exactMatch = enchantment;
                break;
            }

            if (enchantmentString.contains(desiredEnchantmentString)) {
                potentialMatches.add(enchantment);
            }
        }

        if (exactMatch == null && potentialMatches.size() == 1) {
            exactMatch = potentialMatches.getFirst();
        }

        if (exactMatch != null) {
            source.sendFeedback(Component.literal("Exact match found - ")
                    .append(EnchantmentUtils.getEnchantmentName(enchantmentRegistry.wrapAsHolder(exactMatch))));

            sendEnchantmentInfo(source, enchantmentRegistry, exactMatch);

            return 1;
        }

        if (potentialMatches.isEmpty()) {
            source.sendError(Component.literal("No potential matches found!"));

            return 0;
        }

        source.sendFeedback(Component.literal("No exact match found. Potential matches:"));

        for (Enchantment enchantment : potentialMatches) {
            Identifier enchantmentId = enchantmentRegistry.getKey(enchantment);

            if (enchantmentId == null) continue;

            source.sendFeedback(Component.empty()
                    .append(EnchantmentUtils.getEnchantmentName(enchantmentRegistry.wrapAsHolder(enchantment)))
                    .append(" - ")
                    .append(enchantmentId.toString()));
        }

        return 1;
    }

    public static int execute(FabricClientCommandSource source, Holder<Enchantment> enchantmentHolder) {
        Registry<Enchantment> enchantmentRegistry = source.getClient().level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        sendEnchantmentInfo(source, enchantmentRegistry, enchantmentHolder.value());

        return 1;
    }

    public static Component joinItemNames(List<Item> items) {
        MutableComponent text = Component.empty();

        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();

            text = text.append(item.getName(item.getDefaultInstance()));

            if (iterator.hasNext()) text = text.append(", ");
        }

        return text;
    }

    public static Component joinIncompatibleEnchantmentNames(
            Registry<Enchantment> enchantmentRegistry,
            Enchantment baseEnchantment
    ) {
        MutableComponent text = Component.empty();
        Holder<Enchantment> first = enchantmentRegistry.wrapAsHolder(baseEnchantment);

        List<Enchantment> conflicts = enchantmentRegistry.stream().filter(enchantment -> {
            Holder<Enchantment> second = enchantmentRegistry.wrapAsHolder(enchantment);
            if (first.equals(second)) return false;
            return !Enchantment.areCompatible(first, second);
        }).toList();

        if (conflicts.isEmpty()) return Component.literal("N/A");

        Iterator<Enchantment> iterator = conflicts.iterator();
        while (iterator.hasNext()) {
            Enchantment enchantment = iterator.next();

            text.append(EnchantmentUtils.getEnchantmentName(enchantmentRegistry.wrapAsHolder(enchantment)));

            if (iterator.hasNext()) text.append(", ");
        }

        return text;
    }
}
