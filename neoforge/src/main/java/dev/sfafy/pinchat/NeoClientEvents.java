package dev.sfafy.pinchat;

import dev.sfafy.pinchat.gui.MoveableChatScreen;
import dev.sfafy.pinchat.integration.IntegrationManager;
import dev.sfafy.pinchat.keybindings.PinChatKeyBindings;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

public class NeoClientEvents {

  public static void onKeyInput(net.neoforged.neoforge.client.event.InputEvent.Key event) {
    Minecraft client = Minecraft.getInstance();
    if (client.player == null)
      return;

    while (PinChatKeyBindings.openConfigKey.consumeClick()) {
      client.setScreen(IntegrationManager.getConfigScreen(client.screen));
    }

    while (PinChatKeyBindings.openMoveableChatKey.consumeClick()) {
      if (client.screen instanceof MoveableChatScreen) {
        client.setScreen(null);
      } else {
        client.setScreen(new MoveableChatScreen(""));
      }
    }
  }
}
