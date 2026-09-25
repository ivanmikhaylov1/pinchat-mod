package dev.sfafy.pinchat.keybindings;

import dev.sfafy.pinchat.gui.MoveableChatScreen;
import dev.sfafy.pinchat.integration.IntegrationManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class PinChatKeyBindings {
  public static KeyMapping openConfigKey;
  public static KeyMapping openMoveableChatKey;

  public static void register() {
    KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("pinchat", "main"));
    openConfigKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
        "pinchat.hotkey.openConfig", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, category));
    openMoveableChatKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
        "pinchat.hotkey.openMoveableChat", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_U, category));
    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      if (client.player == null) return;
      while (openConfigKey.consumeClick()) client.setScreen(IntegrationManager.getConfigScreen(client.screen));
      while (openMoveableChatKey.consumeClick()) {
        if (client.screen instanceof MoveableChatScreen) client.setScreen(null);
        else if (dev.sfafy.pinchat.config.PinChatConfig.moveableChatEnabled)
          client.setScreen(new MoveableChatScreen(""));
      }
    });
  }
}
