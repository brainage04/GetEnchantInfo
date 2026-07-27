package io.github.brainage04.platform;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.function.BiConsumer;
public interface ClientPlatform {
 void registerClientCommands(BiConsumer<CommandDispatcher<SharedSuggestionProvider>, CommandBuildContext> registrar);
 void registerTooltipCallback(BiConsumer<ItemStack, List<Component>> callback);
}
