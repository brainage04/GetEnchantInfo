package io.github.brainage04.getenchantinfo.fabric;
import com.mojang.brigadier.CommandDispatcher;
import io.github.brainage04.GetEnchantInfo;
import io.github.brainage04.platform.ClientPlatform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.function.BiConsumer;
public final class GetEnchantInfoFabric implements ClientModInitializer, ClientPlatform {
 @Override public void onInitializeClient() { GetEnchantInfo.initialize(this, FabricLoader.getInstance().getConfigDir()); }
 @Override @SuppressWarnings({"unchecked","rawtypes"}) public void registerClientCommands(BiConsumer<CommandDispatcher<SharedSuggestionProvider>, CommandBuildContext> registrar) { ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) -> registrar.accept((CommandDispatcher) dispatcher, context)); }
 @Override public void registerTooltipCallback(BiConsumer<ItemStack,List<Component>> callback) { ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> callback.accept(stack, lines)); }
}
