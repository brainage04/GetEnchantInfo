package io.github.brainage04.commands.core;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.brainage04.commands.*;
import io.github.brainage04.commands.core.argument.ClientHolderReferenceArgumentType;
import io.github.brainage04.platform.ClientPlatform;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
public final class ModCommands {
    private ModCommands() {
    }

    public static void initialize(ClientPlatform platform) {
        platform.registerClientCommands((dispatcher, registryAccess) -> {
            dispatcher.register(LiteralArgumentBuilder.<SharedSuggestionProvider>literal("getenchantinfo")
                    .then(RequiredArgumentBuilder.<SharedSuggestionProvider, Holder.Reference<Enchantment>>argument("enchantmentId", ClientHolderReferenceArgumentType.registryEntry(registryAccess, Registries.ENCHANTMENT))
                            .executes(c -> GetEnchantInfoCommand.execute(c.getSource(), ClientHolderReferenceArgumentType.getEnchantment(c, "enchantmentId"))))
                    .then(RequiredArgumentBuilder.<SharedSuggestionProvider, String>argument("enchantmentName", StringArgumentType.string())
                            .executes(c -> GetEnchantInfoCommand.execute(c.getSource(), StringArgumentType.getString(c, "enchantmentName")))));
            dispatcher.register(LiteralArgumentBuilder.<SharedSuggestionProvider>literal("getenchants")
                    .executes(c -> GetEnchantsCommand.execute(c.getSource()))
                    .then(RequiredArgumentBuilder.<SharedSuggestionProvider, Holder.Reference<Item>>argument("item", ClientHolderReferenceArgumentType.registryEntry(registryAccess, Registries.ITEM))
                            .executes(c -> GetEnchantsCommand.execute(c.getSource(), ClientHolderReferenceArgumentType.getItem(c, "item")))));
            dispatcher.register(LiteralArgumentBuilder.<SharedSuggestionProvider>literal("blacklistedenchants")
                    .then(LiteralArgumentBuilder.<SharedSuggestionProvider>literal("add").then(RequiredArgumentBuilder.<SharedSuggestionProvider, Holder.Reference<Enchantment>>argument("enchantmentId", ClientHolderReferenceArgumentType.registryEntry(registryAccess, Registries.ENCHANTMENT))
                            .executes(c -> BlacklistedEnchantsCommand.executeAdd(c.getSource(), ClientHolderReferenceArgumentType.getEnchantment(c, "enchantmentId")))))
                    .then(LiteralArgumentBuilder.<SharedSuggestionProvider>literal("remove").then(RequiredArgumentBuilder.<SharedSuggestionProvider, Holder.Reference<Enchantment>>argument("enchantmentId", ClientHolderReferenceArgumentType.registryEntry(registryAccess, Registries.ENCHANTMENT))
                            .executes(c -> BlacklistedEnchantsCommand.executeRemove(c.getSource(), ClientHolderReferenceArgumentType.getEnchantment(c, "enchantmentId")))))
                    .then(LiteralArgumentBuilder.<SharedSuggestionProvider>literal("query").executes(c -> BlacklistedEnchantsCommand.executeQuery(c.getSource()))));
        });
    }
}
