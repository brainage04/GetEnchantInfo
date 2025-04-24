package io.github.brainage04.event;

import io.github.brainage04.GetEnchantInfo;
import io.github.brainage04.commands.BlacklistedEnchantsCommand;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ModWorldEvents {
    public static void initialize() {
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register(((client, world) -> {
            BlacklistedEnchantsCommand.getBlacklistedEnchants().clear();

            Optional<Registry<Enchantment>> optionalEnchantmentRegistry = world.getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT);
            if (optionalEnchantmentRegistry.isEmpty()) return;

            Registry<Enchantment> enchantmentRegistry = optionalEnchantmentRegistry.get();

            for (String string : GetEnchantInfo.MOD_CONFIG.blacklistedEnchantmentIds) {
                Identifier identifier = Identifier.of(string);

                Enchantment enchantment = enchantmentRegistry.get(identifier);
                if (enchantment == null) continue;

                BlacklistedEnchantsCommand.getBlacklistedEnchants().add(enchantment);
            }
        }));
    }
}
