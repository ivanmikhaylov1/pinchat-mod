package dev.sfafy.pinchat.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class PinChatCommand {
  public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
    dispatcher.register(ClientCommandManager.literal("pinchat")
        .then(ClientCommandManager.literal("config")
            .executes(context -> {
              MinecraftClient.getInstance().send(() -> {
                MinecraftClient.getInstance().setScreen(dev.sfafy.pinchat.integration.IntegrationManager
                    .getConfigScreen(MinecraftClient.getInstance().currentScreen));
              });

              return 1;
            })));
  }
}
