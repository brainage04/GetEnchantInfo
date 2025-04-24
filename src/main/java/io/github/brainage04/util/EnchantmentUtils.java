package io.github.brainage04.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.util.*;

public class EnchantmentUtils {
    public static Text getEnchantmentName(Registry<Enchantment> enchantmentRegistry, Enchantment enchantment, ItemStack itemStack) {
        MutableText text = Text.empty();
        Text enchantmentName = Enchantment.getName(enchantmentRegistry.getEntry(enchantment), enchantment.getMaxLevel());

        if (itemStack.getEnchantments().getEnchantments().contains(enchantmentRegistry.getEntry(enchantment))) {
            if (itemStack.getEnchantments().getLevel(enchantmentRegistry.getEntry(enchantment)) == enchantment.getMaxLevel()) {
                text = text.append("You already have ")
                        .append(enchantmentName);
            } else {
                text = text.append(enchantmentName)
                        .append(" - you have ")
                        .append(Enchantment.getName(enchantmentRegistry.getEntry(enchantment), itemStack.getEnchantments().getLevel(enchantmentRegistry.getEntry(enchantment))));
            }
        } else {
            text = text.append(enchantmentName);
        }

        return text;
    }

    public static MutableText getEnchantmentName(RegistryEntry<Enchantment> enchantmentRegistryEntry) {
        MutableText mutableText = enchantmentRegistryEntry.value().description.copy();
        if (enchantmentRegistryEntry.isIn(EnchantmentTags.CURSE)) {
            Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.RED));
        } else {
            Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.GRAY));
        }

        return mutableText;
    }

    public static List<Set<Enchantment>> mergeConflicts(List<Pair<Enchantment, Enchantment>> pairs) {
        // 1. Build adjacency map
        Map<Enchantment, Set<Enchantment>> graph = new HashMap<>();
        for (Pair<Enchantment, Enchantment> pair : pairs) {
            Enchantment a = pair.getLeft(), b = pair.getRight();
            graph.computeIfAbsent(a, k -> new HashSet<>()).add(b);
            graph.computeIfAbsent(b, k -> new HashSet<>()).add(a);
        }

        // 2. Track visited nodes
        Set<Enchantment> visited = new HashSet<>();
        List<Set<Enchantment>> components = new ArrayList<>();

        // 3. For each node, if not visited, flood-fill its component
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

    public static Text joinEnchantmentNames(Registry<Enchantment> enchantmentRegistry, Set<Enchantment> enchantments, ItemStack itemStack) {
        MutableText text = Text.empty();

        Iterator<Enchantment> iterator = enchantments.iterator();
        while (iterator.hasNext()) {
            Enchantment enchantment = iterator.next();

            text = text.append(EnchantmentUtils.getEnchantmentName(enchantmentRegistry, enchantment, itemStack));

            if (iterator.hasNext()) text = text.append(", ");
        }

        return text;
    }
}
