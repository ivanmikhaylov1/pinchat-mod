package dev.sfafy.pinchat.keybindings;

import dev.sfafy.pinchat.integration.IntegrationManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class PinChatKeyBindings {
  public static KeyBinding openConfigKey;
  public static KeyBinding openMoveableChatKey;

  public static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of("pinchat", "main"));

  public static void register() {
    openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "pinchat.hotkey.openConfig",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_F8,
        CATEGORY));

    openMoveableChatKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "pinchat.hotkey.openMoveableChat",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_F9,
        CATEGORY));

    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      if (client.player == null) return;
      if (openConfigKey.wasPressed()) {
        client.setScreen(IntegrationManager.getConfigScreen(client.currentScreen));
      }
      if (openMoveableChatKey.wasPressed()) {
        if (client.currentScreen instanceof dev.sfafy.pinchat.gui.MoveableChatScreen) {
          client.setScreen(null);
        } else if (dev.sfafy.pinchat.config.PinChatConfig.moveableChatEnabled) {
          client.setScreen(new dev.sfafy.pinchat.gui.MoveableChatScreen(""));
        }
      }
    });
  }
}
