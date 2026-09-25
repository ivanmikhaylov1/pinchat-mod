package dev.sfafy.pinchat;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PinChatMod implements ClientModInitializer {
  public static final Logger LOGGER = LoggerFactory.getLogger("pinchat");
  public static final String MOD_ID = "pinchat";

  @Override
  public void onInitializeClient() {
    dev.sfafy.pinchat.config.PinChatConfig.load();
    dev.sfafy.pinchat.keybindings.PinChatKeyBindings.register();
    PinnedHudRenderer.register();
    LOGGER.info("PinChat Client Setup");
  }
}
