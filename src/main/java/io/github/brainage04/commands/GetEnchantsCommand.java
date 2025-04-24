package io.github.brainage04.commands;

import io.github.brainage04.util.EnchantmentUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GetEnchantsCommand {
    public static int execute(FabricClientCommandSource source, ItemStack itemStack) {
        Optional<Registry<Enchantment>> optionalEnchantmentRegistry = source.getWorld().getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT);

        if (optionalEnchantmentRegistry.isEmpty()) {
            source.sendError(Text.literal("Enchantment registry could not be found!"));

            return 0;
        }

        Registry<Enchantment> enchantmentRegistry = optionalEnchantmentRegistry.get();

        List<Enchantment> acceptableEnchantments = new ArrayList<>();
        for (Enchantment enchantment : enchantmentRegistry) {
            if (BlacklistedEnchantsCommand.getBlacklistedEnchants().contains(enchantment)) continue;

            if (enchantment.isAcceptableItem(itemStack)) {
                acceptableEnchantments.add(enchantment);
            }
        }

        List<Pair<Enchantment, Enchantment>> conflicts = new ArrayList<>();
        int n = acceptableEnchantments.size();
        for (int i = 0; i < n; i++) {
            Enchantment a = acceptableEnchantments.get(i);

            for (int j = i + 1; j < n; j++) {
                Enchantment b = acceptableEnchantments.get(j);

                if (!Enchantment.canBeCombined(enchantmentRegistry.getEntry(a), enchantmentRegistry.getEntry(b))) {
                    conflicts.add(new Pair<>(a, b));
                }
            }
        }
        List<Set<Enchantment>> mergedConflicts = EnchantmentUtils.mergeConflicts(conflicts);

        for (Set<Enchantment> enchantmentSet : mergedConflicts) {
            for (Enchantment enchantment : enchantmentSet) {
                acceptableEnchantments.remove(enchantment);
            }
        }

        if (mergedConflicts.isEmpty() && acceptableEnchantments.isEmpty()) {
            source.sendError(Text.literal("No acceptable enchantments found!"));
        } else {
            source.sendFeedback(Text.literal("Acceptable enchants for ")
                    .append(itemStack.getName())
                    .append(":")
                    .formatted(Formatting.BOLD));

            if (!mergedConflicts.isEmpty()) {
                source.sendFeedback(Text.literal("Conflicting enchantments (choose one per list):"));

                for (Set<Enchantment> conflictSet : mergedConflicts) {
                    source.sendFeedback(Text.literal(" - ")
                            .append(EnchantmentUtils.joinEnchantmentNames(enchantmentRegistry, conflictSet, itemStack)));
                }
            }

            if (!acceptableEnchantments.isEmpty()) {
                sendEnchantmentMessage(source, itemStack, acceptableEnchantments, enchantmentRegistry, Text.literal("Enchantments with no conflicts:"));
            }
        }

        return 1;
    }

    private static void sendEnchantmentMessage(FabricClientCommandSource source, ItemStack itemStack, List<Enchantment> acceptableEnchantments, Registry<Enchantment> enchantmentRegistry, Text prefix) {
        source.sendFeedback(prefix);

        for (Enchantment enchantment : acceptableEnchantments) {
            source.sendFeedback(Text.literal(" - ")
                    .append(EnchantmentUtils.getEnchantmentName(enchantmentRegistry, enchantment, itemStack)));
        }
    }

    public static int execute(FabricClientCommandSource source, RegistryEntry<Item> item) {
        return execute(source, item.value().getDefaultStack());
    }

    public static int execute(FabricClientCommandSource source) {
        return execute(source, source.getPlayer().getInventory().getSelectedStack());
    }
}
