package dev.sfafy.pinchat.keybindings;

import net.minecraft.resources.Identifier;

import dev.sfafy.pinchat.PinChatMod;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import net.minecraftforge.client.settings.KeyConflictContext;

@Mod.EventBusSubscriber(modid = PinChatMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PinChatKeyBindings {

  public static final KeyMapping.Category CATEGORY = KeyMapping.Category
      .register(Identifier.fromNamespaceAndPath(PinChatMod.MOD_ID, "main"));

  public static KeyMapping openConfigKey;
  public static KeyMapping openMoveableChatKey;

  @SubscribeEvent
  public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
    openConfigKey = new KeyMapping(
        "pinchat.hotkey.openConfig",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_F8,
        CATEGORY,
        100);
    event.register(openConfigKey);

    openMoveableChatKey = new KeyMapping(
        "pinchat.hotkey.openMoveableChat",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_F9,
        CATEGORY,
        100);
    event.register(openMoveableChatKey);

  }

}
