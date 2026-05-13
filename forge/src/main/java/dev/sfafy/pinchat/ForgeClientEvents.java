package dev.sfafy.pinchat;

import dev.sfafy.pinchat.config.PinChatConfig;
import dev.sfafy.pinchat.gui.MoveableChatScreen;
import dev.sfafy.pinchat.integration.IntegrationManager;
import dev.sfafy.pinchat.keybindings.PinChatKeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
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

  @SubscribeEvent
  public static void onChatReceived(ClientChatReceivedEvent event) {
    String plain = event.getMessage().getString();
    if (!KeywordHighlighter.containsKeyword(plain, PinChatConfig.highlightKeywords)) {
      return;
    }
    String highlighted = KeywordHighlighter.highlight(plain, PinChatConfig.highlightKeywords);
    event.setMessage(Component.literal(highlighted));
    Minecraft.getInstance().getSoundManager().play(
        SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F));
  }
}
