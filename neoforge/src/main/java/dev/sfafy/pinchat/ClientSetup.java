package dev.sfafy.pinchat;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class ClientSetup {

  public static void onClientSetup(FMLClientSetupEvent event) {

    dev.sfafy.pinchat.config.PinChatConfig.load();
    PinChatMod.LOGGER.info("PinChat Client Setup");
  }
}
