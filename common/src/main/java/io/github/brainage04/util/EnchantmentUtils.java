package io.github.brainage04.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnchantmentUtils {
    public static Component getEnchantmentName(
            Registry<Enchantment> enchantmentRegistry,
            Enchantment enchantment,
            ItemStack itemStack
    ) {
        Holder<Enchantment> enchantmentHolder = enchantmentRegistry.wrapAsHolder(enchantment);
        MutableComponent text = Component.empty();
        Component enchantmentName = Enchantment.getFullname(enchantmentHolder, enchantment.getMaxLevel());

        if (itemStack.getEnchantments().keySet().contains(enchantmentHolder)) {
            int currentLevel = itemStack.getEnchantments().getLevel(enchantmentHolder);

            if (currentLevel == enchantment.getMaxLevel()) {
                text = text.append("You already have ")
                        .append(enchantmentName);
            } else {
                text = text.append(enchantmentName)
                        .append(" - you have ")
                        .append(Enchantment.getFullname(enchantmentHolder, currentLevel));
            }
        } else {
            text = text.append(enchantmentName);
        }

        return text;
    }

    public static MutableComponent getEnchantmentName(Holder<Enchantment> enchantmentHolder) {
        ChatFormatting formatting = enchantmentHolder.is(EnchantmentTags.CURSE)
                ? ChatFormatting.RED
                : ChatFormatting.GRAY;

        return enchantmentHolder.value().description().copy().withStyle(formatting);
    }

    public static List<Set<Enchantment>> mergeConflicts(List<Pair<Enchantment, Enchantment>> pairs) {
        Map<Enchantment, Set<Enchantment>> graph = new HashMap<>();
        for (Pair<Enchantment, Enchantment> pair : pairs) {
            Enchantment a = pair.getFirst();
            Enchantment b = pair.getSecond();
            graph.computeIfAbsent(a, ignored -> new HashSet<>()).add(b);
            graph.computeIfAbsent(b, ignored -> new HashSet<>()).add(a);
        }

        Set<Enchantment> visited = new HashSet<>();
        List<Set<Enchantment>> components = new ArrayList<>();

        for (Enchantment node : graph.keySet()) {
            if (!visited.contains(node)) {
                Set<Enchantment> comp = new HashSet<>();
                Deque<Enchantment> stack = new ArrayDeque<>();
                stack.push(node);
                visited.add(node);

                while (!stack.isEmpty()) {
                    Enchantment curr = stack.pop();
                    comp.add(curr);
                    for (Enchantment neigh : graph.get(curr)) {
                        if (visited.add(neigh)) {
                            stack.push(neigh);
                        }
                    }
                }

                components.add(comp);
            }
        }

        return components;
    }

    public static Component joinEnchantmentNames(
            Registry<Enchantment> enchantmentRegistry,
            Set<Enchantment> enchantments,
            ItemStack itemStack
    ) {
        MutableComponent text = Component.empty();

        Iterator<Enchantment> iterator = enchantments.iterator();
        while (iterator.hasNext()) {
            Enchantment enchantment = iterator.next();

            text = text.append(EnchantmentUtils.getEnchantmentName(enchantmentRegistry, enchantment, itemStack));

            if (iterator.hasNext()) text = text.append(", ");
        }

        return text;
    }
}
