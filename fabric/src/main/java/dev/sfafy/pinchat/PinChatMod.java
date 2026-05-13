package dev.sfafy.pinchat;

import dev.sfafy.pinchat.command.PinChatCommand;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PinChatMod implements ClientModInitializer {
  public static final Logger LOGGER = LoggerFactory.getLogger("pinchat");
  public static final String MOD_ID = "pinchat";

  public void onInitializeClient() {
    dev.sfafy.pinchat.integration.IntegrationManager.detectMods();
    dev.sfafy.pinchat.config.PinChatConfig.load();
    dev.sfafy.pinchat.keybindings.PinChatKeyBindings.register();

    if (dev.sfafy.pinchat.integration.IntegrationManager.isMalilibLoaded()) {
      dev.sfafy.pinchat.integration.MalilibIntegration.init();
    }

    ClientCommandRegistrationCallback.EVENT
        .register((dispatcher, registryAccess) -> PinChatCommand.register(dispatcher));

    ClientReceiveMessageEvents.MODIFY_GAME.register((message, overlay) -> {
      var keywords = dev.sfafy.pinchat.config.PinChatConfig.highlightKeywords;
      String plain = message.getString();
      if (!KeywordHighlighter.containsKeyword(plain, keywords)) {
        return message;
      }
      MinecraftClient.getInstance().getSoundManager().play(
          PositionedSoundInstance.master(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F));
      return Text.literal(KeywordHighlighter.highlight(plain, keywords));
    });

    LOGGER.info("PinChatMod initialized!");

    HudRenderCallback.EVENT.register(new PinnedHudRenderer());
  }
}
