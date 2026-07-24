package io.github.brainage04;

import io.github.brainage04.config.ModConfig;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestDedicatedServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerConnection;

import java.util.List;
import java.util.Properties;

@SuppressWarnings("UnstableApiUsage")
public final class GetEnchantInfoClientGameTest implements FabricClientGameTest {
    private static final List<String> COMMANDS = List.of("getenchantinfo", "getenchants", "blacklistedenchants");

    @Override
    public void runTest(ClientGameTestContext context) {
        Properties serverProperties = new Properties();
        serverProperties.setProperty("simulation-distance", "5");
        serverProperties.setProperty("view-distance", "2");

        try (TestDedicatedServerContext server = context.worldBuilder().createServer(serverProperties);
             TestServerConnection ignored = server.connect()) {
            context.computeOnClient(client -> {
                if (client.level == null || client.player == null || client.getConnection() == null) {
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
    }
}
