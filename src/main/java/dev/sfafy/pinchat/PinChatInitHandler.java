package dev.sfafy.pinchat;

import dev.sfafy.pinchat.config.PinChatConfigMalilib;
import dev.sfafy.pinchat.input.PinChatInputHandler;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;

public class PinChatInitHandler implements IInitializationHandler {
  @Override
  public void registerModHandlers() {
    PinChatMod.LOGGER.info("=== PinChatInitHandler: registerModHandlers() called by MaLiLib ===");
    PinChatMod.LOGGER.info("Registering PinChat config handlers...");
    ConfigManager.getInstance().registerConfigHandler(Reference.MOD_INFO.getModId(), new PinChatConfigMalilib());
    PinChatMod.LOGGER.info("Config handler registered, now registering keybinds...");
    PinChatInputHandler.getInstance().registerKeybinds();
    PinChatMod.LOGGER.info("=== PinChat config handlers and keybinds registered successfully ===");
  }
}
