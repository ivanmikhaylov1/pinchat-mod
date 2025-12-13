package dev.sfafy.pinchat.input;

import dev.sfafy.pinchat.PinChatMod;
import dev.sfafy.pinchat.gui.PinChatConfigGui;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.*;

public class PinChatInputHandler implements IKeybindProvider, IHotkeyCallback {
  public static final ConfigHotkey OPEN_CONFIG_GUI = new ConfigHotkey("openConfigGui", "P,C",
      "pinchat.hotkey.openConfig");
  public static final ConfigHotkey OPEN_MOVEABLE_CHAT = new ConfigHotkey("openMoveableChat", "U",
      "pinchat.hotkey.openMoveableChat");
  private static final PinChatInputHandler INSTANCE = new PinChatInputHandler();

  public static PinChatInputHandler getInstance() {
    return INSTANCE;
  }

  public void registerKeybinds() {
    PinChatMod.LOGGER.info("PinChatInputHandler: Registering keybind provider...");
    fi.dy.masa.malilib.event.InputEventHandler.getKeybindManager().registerKeybindProvider(this);
    PinChatMod.LOGGER.info("PinChatInputHandler: Keybind provider registered.");
  }

  @Override
  public void addKeysToMap(IKeybindManager manager) {
    PinChatMod.LOGGER.info("PinChatInputHandler: addKeysToMap called");
    PinChatMod.LOGGER.info("PinChatInputHandler: Adding OPEN_CONFIG_GUI keybind: {}",
        OPEN_CONFIG_GUI.getKeybind().getKeysDisplayString());
    manager.addKeybindToMap(OPEN_CONFIG_GUI.getKeybind());
    PinChatMod.LOGGER.info("PinChatInputHandler: Adding OPEN_MOVEABLE_CHAT keybind: {}",
        OPEN_MOVEABLE_CHAT.getKeybind().getKeysDisplayString());
    manager.addKeybindToMap(OPEN_MOVEABLE_CHAT.getKeybind());
    OPEN_CONFIG_GUI.getKeybind().setCallback(this);
    OPEN_MOVEABLE_CHAT.getKeybind().setCallback(this);
    PinChatMod.LOGGER.info("PinChatInputHandler: Callbacks set for both keybinds");
  }

  @Override
  public void addHotkeys(IKeybindManager manager) {
    PinChatMod.LOGGER.info("PinChatInputHandler: addHotkeys called");
  }

  @Override
  public boolean onKeyAction(KeyAction action, IKeybind key) {
    PinChatMod.LOGGER.info("PinChatInputHandler: onKeyAction called - action: {}, key: {}", action,
        key.getKeysDisplayString());

    if (key == OPEN_CONFIG_GUI.getKeybind()) {
      PinChatMod.LOGGER.info("PinChatInputHandler: Opening config GUI");
      PinChatConfigGui gui = new PinChatConfigGui(null);
      gui.setParent(net.minecraft.client.MinecraftClient.getInstance().currentScreen);
      fi.dy.masa.malilib.gui.GuiBase.openGui(gui);
      return true;
    }
    if (key == OPEN_MOVEABLE_CHAT.getKeybind()) {
      PinChatMod.LOGGER.info("PinChatInputHandler: Opening moveable chat");
      net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
      if (client.currentScreen instanceof dev.sfafy.pinchat.gui.MoveableChatScreen) {
        PinChatMod.LOGGER.info("PinChatInputHandler: Closing moveable chat (already open)");
        client.setScreen(null);
      } else {
        PinChatMod.LOGGER.info("PinChatInputHandler: Creating new MoveableChatScreen");
        client.setScreen(new dev.sfafy.pinchat.gui.MoveableChatScreen(""));
      }
      return true;
    }
    PinChatMod.LOGGER.info("PinChatInputHandler: Key not recognized, returning false");
    return false;
  }
}
