package io.github.brainage04.commands;

import io.github.brainage04.util.EnchantmentUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class GetEnchantInfoCommand {
    public static void sendEnchantmentInfo(FabricClientCommandSource source, Registry<Enchantment> enchantmentRegistry, Enchantment enchantment) {
        Identifier enchantmentId = enchantmentRegistry.getId(enchantment);
        if (enchantmentId == null) return;

        source.sendFeedback(Text.literal("Enchant info for ")
                .append(EnchantmentUtils.getEnchantmentName(enchantmentRegistry.getEntry(enchantment)))
                .append(":")
                .formatted(Formatting.BOLD));

        source.sendFeedback(Text.literal("ID: %s".formatted(enchantmentId.toString())));
        source.sendFeedback(Text.literal("Max level: %d".formatted(enchantment.getMaxLevel())));
        source.sendFeedback(Text.literal("Incompatible with: ")
                .append(joinIncompatibleEnchantmentNames(enchantmentRegistry, enchantment)));
        source.sendFeedback(Text.literal("Applied to: ")
                .append(joinItemNames(enchantment.getApplicableItems().stream().map(RegistryEntry::value).toList())));
    }

    public static int execute(FabricClientCommandSource source, String desiredEnchantmentString) {
        Optional<Registry<Enchantment>> optionalEnchantmentRegistry = source.getWorld().getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT);

        if (optionalEnchantmentRegistry.isEmpty()) {
            source.sendFeedback(Text.literal("Enchantment registry could not be found!"));

            return 0;
        }

        Registry<Enchantment> enchantmentRegistry = optionalEnchantmentRegistry.get();

        Enchantment exactMatch = null;
        List<Enchantment> potentialMatches = new ArrayList<>();
        for (Enchantment enchantment : enchantmentRegistry) {
            String enchantmentString = EnchantmentUtils.getEnchantmentName(enchantmentRegistry.getEntry(enchantment)).getString().toLowerCase();

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
            source.sendFeedback(Text.literal("Exact match found - ")
                    .append(EnchantmentUtils.getEnchantmentName(enchantmentRegistry.getEntry(exactMatch))));

            sendEnchantmentInfo(source, enchantmentRegistry, exactMatch);

            return 1;
        }

        if (potentialMatches.isEmpty()) {
            source.sendError(Text.literal("No potential matches found!"));

            return 0;
        }

        source.sendFeedback(Text.literal("No exact match found. Potential matches:"));

        for (Enchantment enchantment : potentialMatches) {
            Identifier enchantmentId = enchantmentRegistry.getId(enchantment);

            if (enchantmentId == null) continue;

            source.sendFeedback(Text.empty()
                    .append(EnchantmentUtils.getEnchantmentName(enchantmentRegistry.getEntry(enchantment)))
                    .append(" - ")
                    .append(enchantmentId.toString()));
        }

        return 1;
    }

    public static int execute(FabricClientCommandSource source, RegistryEntry<Enchantment> enchantmentRegistryEntry) {
        Optional<Registry<Enchantment>> optionalEnchantmentRegistry = source.getWorld().getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT);

        if (optionalEnchantmentRegistry.isEmpty()) {
            source.sendFeedback(Text.literal("Enchantment registry could not be found!"));

            return 0;
        }

        Registry<Enchantment> enchantmentRegistry = optionalEnchantmentRegistry.get();

        sendEnchantmentInfo(source, enchantmentRegistry, enchantmentRegistryEntry.value());

        return 1;
    }

    public static Text joinItemNames(List<Item> items) {
        MutableText text = Text.empty();

        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();

            text = text.append(item.getName());

            if (iterator.hasNext()) text = text.append(", ");
        }

        return text;
    }

    public static Text joinIncompatibleEnchantmentNames(Registry<Enchantment> enchantmentRegistry, Enchantment baseEnchantment) {
        MutableText text = Text.empty();

        RegistryEntry<Enchantment> first = enchantmentRegistry.getEntry(baseEnchantment);

        List<Enchantment> conflicts = enchantmentRegistry.stream().filter(enchantment -> {
            RegistryEntry<Enchantment> second = enchantmentRegistry.getEntry(enchantment);
            if (first.equals(second)) return false;
            return !Enchantment.canBeCombined(first, second);
        }).toList();

        if (conflicts.isEmpty()) return Text.literal("N/A");

        Iterator<Enchantment> iterator = conflicts.iterator();
        while (iterator.hasNext()) {
            Enchantment enchantment = iterator.next();

            text.append(EnchantmentUtils.getEnchantmentName(enchantmentRegistry.getEntry(enchantment)));

            if (iterator.hasNext()) text.append(", ");
        }

        return text;
    }
}
