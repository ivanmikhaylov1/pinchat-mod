package dev.sfafy.pinchat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class PinChatFabric implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    PinChatCommon.init();

    HudRenderCallback.EVENT
        .register((context, tickCounter) -> {
          new PinnedHudRenderer().render(context, 0f);
        });
  }
}