package dev.sfafy.pinchat;

import dev.sfafy.pinchat.command.PinChatCommand;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PinChatMod implements ClientModInitializer {
  public static final Logger LOGGER = LoggerFactory.getLogger("pinchat");
  public static final String MOD_ID = "pinchat";

  public void onInitializeClient() {
    dev.sfafy.pinchat.integration.IntegrationManager.detectMods();
    dev.sfafy.pinchat.config.PinChatConfig.load();
    dev.sfafy.pinchat.keybindings.PinChatKeyBindings.register();

    if (dev.sfafy.pinchat.integration.IntegrationManager.isMalilibLoaded()) {
      dev.sfafy.pinchat.integration.MalilibIntegration.init();
    }

    ClientCommandRegistrationCallback.EVENT
        .register((dispatcher, registryAccess) -> PinChatCommand.register(dispatcher));

    LOGGER.info("PinChatMod initialized!");

    HudRenderCallback.EVENT.register(new PinnedHudRenderer());
  }
}
