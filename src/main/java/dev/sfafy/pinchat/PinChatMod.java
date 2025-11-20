package dev.sfafy.pinchat;

import dev.sfafy.pinchat.command.PinChatCommand;
import dev.sfafy.pinchat.config.PinChatConfigMalilib;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PinChatMod implements ClientModInitializer {
  public static final Logger LOGGER = LoggerFactory.getLogger("pinchat");
  public static final String MOD_ID = "pinchat";

  @Override
  public void onInitializeClient() {

    InitializationHandler.getInstance().registerInitializationHandler(new PinChatInitHandler());
    PinChatConfigMalilib.loadConfig();

    ClientCommandRegistrationCallback.EVENT
        .register((dispatcher, registryAccess) -> PinChatCommand.register(dispatcher));

    LOGGER.info("PinChatMod initialized!");

    HudRenderCallback.EVENT.register(new PinnedHudRenderer());
  }
}