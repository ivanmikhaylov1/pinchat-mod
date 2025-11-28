package dev.sfafy.pinchat.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.sfafy.pinchat.gui.PinChatConfigGui;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class PinChatCommand {
  public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
    dispatcher.register(ClientCommandManager.literal("pinchat")
        .then(ClientCommandManager.literal("config")
            .executes(context -> {
              MinecraftClient.getInstance().send(() -> {
                PinChatConfigGui gui = new PinChatConfigGui(null);
                gui.setParent(MinecraftClient.getInstance().currentScreen);
                fi.dy.masa.malilib.gui.GuiBase.openGui(gui);
              });
              return 1;
            })
        )
    );
  }
}
