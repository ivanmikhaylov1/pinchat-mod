package dev.sfafy.pinchat.keybindings;

import net.minecraft.resources.Identifier;

import dev.sfafy.pinchat.PinChatMod;
import dev.sfafy.pinchat.integration.IntegrationManager;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.settings.KeyConflictContext;

@Mod.EventBusSubscriber(modid = PinChatMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PinChatKeyBindings {

  public static final KeyMapping.Category CATEGORY = KeyMapping.Category
      .register(Identifier.fromNamespaceAndPath(PinChatMod.MOD_ID, "main"));

  public static KeyMapping openConfigKey;
  public static KeyMapping openMoveableChatKey;

  public static final KeyMapping OPEN_CHAT_HISTORY = new KeyMapping(
      "key.pinchat.open_history",
      KeyConflictContext.IN_GAME,
      InputConstants.Type.KEYSYM,
      GLFW.GLFW_KEY_H,
      CATEGORY,
      100);

  public static final KeyMapping TOGGLE_PIN = new KeyMapping(
      "key.pinchat.toggle_pin",
      KeyConflictContext.GUI,
      InputConstants.Type.KEYSYM,
      GLFW.GLFW_KEY_UNKNOWN,
      CATEGORY,
      100);

  @SubscribeEvent
  public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
    openConfigKey = new KeyMapping(
        "pinchat.hotkey.openConfig",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_P,
        CATEGORY,
        100);
    event.register(openConfigKey);

    openMoveableChatKey = new KeyMapping(
        "pinchat.hotkey.openMoveableChat",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_U,
        CATEGORY,
        100);
    event.register(openMoveableChatKey);

    event.register(OPEN_CHAT_HISTORY);
    event.register(TOGGLE_PIN);
  }

}
