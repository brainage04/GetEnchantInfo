package io.github.brainage04.commands.core;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.brainage04.commands.BlacklistedEnchantsCommand;
import io.github.brainage04.commands.GetEnchantInfoCommand;
import io.github.brainage04.commands.GetEnchantsCommand;
import io.github.brainage04.commands.core.argument.ClientRegistryEntryReferenceArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.registry.RegistryKeys;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ModCommands {
    public static void initialize() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("getenchantinfo")
                                .then(argument("enchantmentId", ClientRegistryEntryReferenceArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT))
                                        .executes(context ->
                                                GetEnchantInfoCommand.execute(
                                                        context.getSource(),
                                                        ClientRegistryEntryReferenceArgumentType.getEnchantment(context, "enchantmentId")
                                                )
                                        )
                                )
                                .then(argument("enchantmentName", StringArgumentType.string())
                                        .executes(context ->
                                                GetEnchantInfoCommand.execute(
                                                        context.getSource(),
                                                        StringArgumentType.getString(context, "enchantmentName")
                                                )
                                        )
                                )
                )
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("getenchants")
                                .executes(context ->
                                        GetEnchantsCommand.execute(
                                                context.getSource()
                                        )
                                )
                                .then(argument("item", ClientRegistryEntryReferenceArgumentType.registryEntry(registryAccess, RegistryKeys.ITEM))
                                        .executes(context ->
                                                GetEnchantsCommand.execute(
                                                        context.getSource(),
                                                        ClientRegistryEntryReferenceArgumentType.getItem(context, "item")
                                                )
                                        )
                                )
                )
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("blacklistedenchants")
                                .then(literal("add")
                                        .then(argument("enchantmentId", ClientRegistryEntryReferenceArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT))
                                                .executes(context ->
                                                        BlacklistedEnchantsCommand.executeAdd(
                                                                context.getSource(),
                                                                ClientRegistryEntryReferenceArgumentType.getEnchantment(context, "enchantmentId")
                                                        )
                                                )
                                        )
                                )
                                .then(literal("remove")
                                        .then(argument("enchantmentId", ClientRegistryEntryReferenceArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT))
                                                .executes(context ->
                                                        BlacklistedEnchantsCommand.executeRemove(
                                                                context.getSource(),
                                                                ClientRegistryEntryReferenceArgumentType.getEnchantment(context, "enchantmentId")
                                                        )
                                                )
                                        )
                                )
                                .then(literal("query")
                                        .executes(context ->
                                                BlacklistedEnchantsCommand.executeQuery(
                                                        context.getSource()
                                                )
                                        )
                                )
                )
        );
    }
}
