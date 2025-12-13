package dev.sfafy.pinchat;

import dev.sfafy.pinchat.gui.MoveableChatScreen;
import dev.sfafy.pinchat.integration.IntegrationManager;
import dev.sfafy.pinchat.keybindings.PinChatKeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PinChatMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeClientEvents {

  @SubscribeEvent
  public static void onKeyInput(net.minecraftforge.client.event.InputEvent.Key event) {
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
