package io.github.brainage04.commands.core.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ClientRegistryEntryReferenceArgumentType<T> implements ArgumentType<RegistryEntry.Reference<T>> {
    public static final Dynamic2CommandExceptionType NOT_FOUND_EXCEPTION = new Dynamic2CommandExceptionType((element, type) ->
            Text.stringifiedTranslatable("argument.resource.not_found", element, type));
    public static final Dynamic3CommandExceptionType INVALID_TYPE_EXCEPTION = new Dynamic3CommandExceptionType((element, type, expectedType) ->
            Text.stringifiedTranslatable("argument.resource.invalid_type", element, type, expectedType));

    final RegistryKey<? extends Registry<T>> registryRef;
    private final RegistryWrapper<T> registryWrapper;

    public ClientRegistryEntryReferenceArgumentType(CommandRegistryAccess registryAccess, RegistryKey<? extends Registry<T>> registryRef) {
        this.registryRef = registryRef;
        this.registryWrapper = registryAccess.getOrThrow(registryRef);
    }

    public static <T> ClientRegistryEntryReferenceArgumentType<T> registryEntry(CommandRegistryAccess registryAccess, RegistryKey<? extends Registry<T>> registryRef) {
        return new ClientRegistryEntryReferenceArgumentType<>(registryAccess, registryRef);
    }

    public static <T> RegistryEntry.Reference<T> getRegistryEntry(CommandContext<FabricClientCommandSource> context, String name, RegistryKey<Registry<T>> registryRef) throws CommandSyntaxException {
        @SuppressWarnings("unchecked")
        RegistryEntry.Reference<T> reference = context.getArgument(name, RegistryEntry.Reference.class);
        RegistryKey<?> registryKey = reference.registryKey();
        if (registryKey.isOf(registryRef)) {
            return reference;
        } else {
            throw INVALID_TYPE_EXCEPTION.create(registryKey.getValue(), registryKey.getRegistry(), registryRef.getValue());
        }
    }

    public static RegistryEntry.Reference<Enchantment> getEnchantment(CommandContext<FabricClientCommandSource> context, String name) throws CommandSyntaxException {
        return getRegistryEntry(context, name, RegistryKeys.ENCHANTMENT);
    }

    public static RegistryEntry.Reference<Item> getItem(CommandContext<FabricClientCommandSource> context, String name) throws CommandSyntaxException {
        return getRegistryEntry(context, name, RegistryKeys.ITEM);
    }

    public RegistryEntry.Reference<T> parse(StringReader stringReader) throws CommandSyntaxException {
        Identifier identifier = Identifier.fromCommandInput(stringReader);
        RegistryKey<T> registryKey = RegistryKey.of(this.registryRef, identifier);
        return this.registryWrapper.getOptional(registryKey).orElseThrow(() ->
                NOT_FOUND_EXCEPTION.createWithContext(stringReader, identifier, this.registryRef.getValue()));
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(this.registryWrapper.streamKeys().map(RegistryKey::getValue), builder);
    }
}
