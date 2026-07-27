package io.github.brainage04.getenchantinfo.neoforge;
import com.mojang.brigadier.CommandDispatcher;
import io.github.brainage04.GetEnchantInfo;
import io.github.brainage04.platform.ClientPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import java.util.List;
import java.util.function.BiConsumer;
@Mod(value=GetEnchantInfo.MOD_ID, dist=Dist.CLIENT)
public final class GetEnchantInfoNeoForge implements ClientPlatform {
 public GetEnchantInfoNeoForge(IEventBus modBus) { GetEnchantInfo.initialize(this, FMLPaths.CONFIGDIR.get()); }
 @Override @SuppressWarnings({"unchecked","rawtypes"}) public void registerClientCommands(BiConsumer<CommandDispatcher<SharedSuggestionProvider>, CommandBuildContext> registrar) { NeoForge.EVENT_BUS.addListener((RegisterClientCommandsEvent event) -> registrar.accept((CommandDispatcher) event.getDispatcher(), event.getBuildContext())); }
 @Override public void registerTooltipCallback(BiConsumer<ItemStack,List<Component>> callback) { NeoForge.EVENT_BUS.addListener((ItemTooltipEvent event) -> callback.accept(event.getItemStack(), event.getToolTip())); }
}
