package io.github.brainage04;

import io.github.brainage04.config.ModConfig;
import io.github.brainage04.fabricmoddingconventions.ClientGameTestRecorder;
import io.github.brainage04.fabricmoddingconventions.ClientGameTestServers;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestDedicatedServerContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;
import java.util.Properties;

@SuppressWarnings("UnstableApiUsage")
public final class GetEnchantInfoClientGameTest implements FabricClientGameTest {
    private static final List<String> COMMANDS = List.of("getenchantinfo", "getenchants", "blacklistedenchants");

    @Override
    public void runTest(ClientGameTestContext context) {
        Properties serverProperties = ClientGameTestServers.flatServerProperties();

        try (TestDedicatedServerContext server = context.worldBuilder().createServer(serverProperties)) {
            ClientGameTestServers.connectToDedicatedServer(context, server, "GetEnchantInfo command recording GameTest");
            try {
                server.runOnServer(minecraftServer -> preparePlayer(
                        minecraftServer.getPlayerList().getPlayers().getFirst(),
                        minecraftServer.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                ));
                ClientGameTestServers.assertClientWorldAndPlayerAvailable(context);
                assertCommandsAndDefaults(context);
                context.waitTicks(20);

                ClientGameTestRecorder.startRecording(context);
                ClientGameTestRecorder.showStep(
                        context,
                        "getenchantinfo.item",
                        "Enchanted sword ready",
                        "A Sharpness V and Unbreaking III diamond sword is selected for enchantment analysis"
                );
                context.waitTicks(20);

                runCommand(context, "getenchants");
                ClientGameTestRecorder.showStep(
                        context,
                        "getenchantinfo.compatible",
                        "Compatible enchantments",
                        "/getenchants renders compatible enchantments and conflict groups for the selected sword"
                );
                context.waitTicks(40);

                runCommand(context, "getenchantinfo minecraft:sharpness");
                ClientGameTestRecorder.showStep(
                        context,
                        "getenchantinfo.details",
                        "Sharpness details",
                        "/getenchantinfo renders the enchantment ID, maximum level, incompatibilities, and supported items"
                );
                context.waitTicks(40);

                runCommand(context, "blacklistedenchants query");
                ClientGameTestRecorder.showStep(
                        context,
                        "getenchantinfo.blacklist",
                        "Default enchantment blacklist",
                        "/blacklistedenchants query renders the default excluded enchantments"
                );
                context.waitTicks(40);
            } finally {
                ClientGameTestServers.disconnectFromDedicatedServer(context);
            }
        }
    }

    private static void preparePlayer(ServerPlayer player, Registry<Enchantment> enchantments) {
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        sword.enchant(enchantment(enchantments, Enchantments.SHARPNESS), 5);
        sword.enchant(enchantment(enchantments, Enchantments.UNBREAKING), 3);

        player.getInventory().clearContent();
        player.getInventory().setItem(0, sword);
        player.getInventory().setSelectedSlot(0);
    }

    private static Holder<Enchantment> enchantment(Registry<Enchantment> enchantments, ResourceKey<Enchantment> key) {
        return enchantments.getOrThrow(key);
    }

    private static void assertCommandsAndDefaults(ClientGameTestContext context) {
        context.computeOnClient(client -> {
            if (client.getConnection() == null) {
                throw new AssertionError("Expected a connected client for command verification.");
            }

            for (String command : COMMANDS) {
                if (client.getConnection().getCommands().getRoot().getChild(command) == null) {
                    throw new AssertionError("Expected client command /" + command + " to register.");
                }
            }

            ModConfig defaults = new ModConfig();
            if (defaults.blacklistedEnchantmentIds.size() != 10
                    || !defaults.blacklistedEnchantmentIds.containsAll(List.of(
                    "minecraft:binding_curse",
                    "minecraft:vanishing_curse",
                    "minecraft:blast_protection",
                    "minecraft:projectile_protection",
                    "minecraft:fire_protection",
                    "minecraft:thorns",
                    "minecraft:bane_of_arthropods",
                    "minecraft:smite",
                    "minecraft:knockback",
                    "minecraft:frost_walker"
            ))) {
                throw new AssertionError("Expected the complete default enchantment blacklist.");
            }
            return null;
        });
    }

    private static void runCommand(ClientGameTestContext context, String command) {
        context.runOnClient(client -> {
            if (client.getConnection() == null) {
                throw new AssertionError("Expected a connected client to run /" + command + '.');
            }
            client.getConnection().sendCommand(command);
        });
    }
}
