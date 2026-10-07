package dev.sfafy.pinchat.keybindings;

import net.minecraft.resources.Identifier;

import dev.sfafy.pinchat.PinChatMod;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

public class PinChatKeyBindings {

  public static final KeyMapping.Category CATEGORY = KeyMapping.Category
      .register(Identifier.fromNamespaceAndPath(PinChatMod.MOD_ID, "main"));

  public static KeyMapping openConfigKey;
  public static KeyMapping openMoveableChatKey;

  public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
    openConfigKey = new KeyMapping(
        "pinchat.hotkey.openConfig",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_F8,
        CATEGORY);
    event.register(openConfigKey);

    openMoveableChatKey = new KeyMapping(
        "pinchat.hotkey.openMoveableChat",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_F9,
        CATEGORY);
    event.register(openMoveableChatKey);

  }

}
