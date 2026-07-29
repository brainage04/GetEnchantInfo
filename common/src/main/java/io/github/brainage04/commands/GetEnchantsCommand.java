package io.github.brainage04.commands;

import net.minecraft.client.Minecraft;

import com.mojang.datafixers.util.Pair;
import io.github.brainage04.util.EnchantmentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GetEnchantsCommand {
    public static int execute(net.minecraft.commands.SharedSuggestionProvider source, ItemStack itemStack) {
        Registry<Enchantment> enchantmentRegistry = Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        syncBlacklist(enchantmentRegistry);

        List<Enchantment> acceptableEnchantments = new ArrayList<>();
        for (Enchantment enchantment : enchantmentRegistry) {
            if (BlacklistedEnchantsCommand.getBlacklistedEnchants().contains(enchantment)) continue;

            if (enchantment.canEnchant(itemStack)) {
                acceptableEnchantments.add(enchantment);
            }
        }

        List<Pair<Enchantment, Enchantment>> conflicts = new ArrayList<>();
        int n = acceptableEnchantments.size();
        for (int i = 0; i < n; i++) {
            Enchantment a = acceptableEnchantments.get(i);

            for (int j = i + 1; j < n; j++) {
                Enchantment b = acceptableEnchantments.get(j);

                if (!Enchantment.areCompatible(enchantmentRegistry.wrapAsHolder(a), enchantmentRegistry.wrapAsHolder(b))) {
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
            feedback(Component.literal("No acceptable enchantments found!"));
        } else {
            feedback(Component.literal("Acceptable enchants for ")
                    .append(itemStack.getHoverName())
                    .append(":")
                    .withStyle(ChatFormatting.BOLD));

            if (!mergedConflicts.isEmpty()) {
                feedback(Component.literal("Conflicting enchantments (choose one per list):"));

                for (Set<Enchantment> conflictSet : mergedConflicts) {
                    feedback(Component.literal(" - ")
                            .append(EnchantmentUtils.joinEnchantmentNames(enchantmentRegistry, conflictSet, itemStack)));
                }
            }

            if (!acceptableEnchantments.isEmpty()) {
                sendEnchantmentMessage(
                        source,
                        itemStack,
                        acceptableEnchantments,
                        enchantmentRegistry,
                        Component.literal("Enchantments with no conflicts:")
                );
            }
        }

        return 1;
    }

    private static void sendEnchantmentMessage(
            net.minecraft.commands.SharedSuggestionProvider source,
            ItemStack itemStack,
            List<Enchantment> acceptableEnchantments,
            Registry<Enchantment> enchantmentRegistry,
            Component prefix
    ) {
        feedback(prefix);

        for (Enchantment enchantment : acceptableEnchantments) {
            feedback(Component.literal(" - ")
                    .append(EnchantmentUtils.getEnchantmentName(enchantmentRegistry, enchantment, itemStack)));
        }
    }

    public static int execute(net.minecraft.commands.SharedSuggestionProvider source, Holder<Item> item) {
        return execute(source, item.value().getDefaultInstance());
    }

    public static int execute(net.minecraft.commands.SharedSuggestionProvider source) {
        return execute(source, Minecraft.getInstance().player.getInventory().getSelectedItem());
    }

    private static void syncBlacklist(Registry<Enchantment> enchantmentRegistry) {
        BlacklistedEnchantsCommand.getBlacklistedEnchants().clear();

        for (String string : io.github.brainage04.GetEnchantInfo.MOD_CONFIG.blacklistedEnchantmentIds) {
            enchantmentRegistry
                    .getOptional(net.minecraft.resources.Identifier.parse(string))
                    .ifPresent(BlacklistedEnchantsCommand.getBlacklistedEnchants()::add);
        }
    }
    private static void feedback(net.minecraft.network.chat.Component message) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) minecraft.player.sendSystemMessage(message);
    }

}
