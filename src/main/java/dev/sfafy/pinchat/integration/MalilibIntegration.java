package dev.sfafy.pinchat.integration;

import net.minecraft.client.gui.screen.Screen;

public class MalilibIntegration {
  public static void init() {
    dev.sfafy.pinchat.PinChatMod.LOGGER.info("Initializing Malilib integration");
    fi.dy.masa.malilib.event.InitializationHandler.getInstance()
        .registerInitializationHandler(new dev.sfafy.pinchat.PinChatInitHandler());
  }

  public static Screen createConfigScreen(Screen parent) {
    dev.sfafy.pinchat.gui.PinChatConfigGui gui = new dev.sfafy.pinchat.gui.PinChatConfigGui(null);
    gui.setParent(parent);
    return gui;
  }
}
