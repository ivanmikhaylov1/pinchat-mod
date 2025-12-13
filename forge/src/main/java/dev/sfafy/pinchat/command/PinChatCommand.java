package dev.sfafy.pinchat.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import dev.sfafy.pinchat.PinChatMod;

import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

@Mod.EventBusSubscriber(modid = PinChatMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PinChatCommand {

  @SubscribeEvent
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
