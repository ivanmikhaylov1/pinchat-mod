package dev.sfafy.pinchat.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import dev.sfafy.pinchat.PinChatMod;

import net.neoforged.bus.api.SubscribeEvent;

public class PinChatCommand {

  public static void onRegisterCommands(RegisterClientCommandsEvent event) {
    event.getDispatcher().register(Commands.literal("pinchat")
        .then(Commands.literal("config")
            .executes(context -> {
              Minecraft.getInstance().execute(() -> {
                Minecraft.getInstance().setScreen(dev.sfafy.pinchat.integration.IntegrationManager
                    .getConfigScreen(Minecraft.getInstance().screen));
              });

              return 1;
            })));
  }
}
