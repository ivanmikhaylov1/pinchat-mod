package dev.sfafy.pinchat.keybindings;

import dev.sfafy.pinchat.integration.IntegrationManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class PinChatKeyBindings {
  public static KeyBinding openConfigKey;
  public static KeyBinding openMoveableChatKey;

  public static void register() {
    openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "pinchat.hotkey.openConfig",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_P,
        KeyBinding.Category.GAMEPLAY));

    openMoveableChatKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "pinchat.hotkey.openMoveableChat",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_U,
        KeyBinding.Category.GAMEPLAY));

    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      if (openConfigKey.wasPressed()) {
        client.setScreen(IntegrationManager.getConfigScreen(client.currentScreen));
      }
      if (openMoveableChatKey.wasPressed()) {
        if (client.currentScreen instanceof dev.sfafy.pinchat.gui.MoveableChatScreen) {
          client.setScreen(null);
        } else {
          client.setScreen(new dev.sfafy.pinchat.gui.MoveableChatScreen(""));
        }
      }
    });
  }
}
